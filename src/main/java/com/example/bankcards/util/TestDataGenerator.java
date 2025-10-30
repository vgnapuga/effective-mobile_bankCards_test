package com.example.bankcards.util;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.card.CardStatus;
import com.example.bankcards.model.card.vo.CardBalance;
import com.example.bankcards.model.card.vo.CardExpiryDate;
import com.example.bankcards.model.card.vo.CardNumber;
import com.example.bankcards.model.transfer.Transfer;
import com.example.bankcards.model.transfer.vo.Amount;
import com.example.bankcards.model.user.Role;
import com.example.bankcards.model.user.User;
import com.example.bankcards.model.user.vo.Email;
import com.example.bankcards.model.user.vo.Password;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.CardEncryption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class TestDataGenerator {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardEncryption cardEncryption;

    private final Random random = new Random();

    @Bean
    public CommandLineRunner generateTestData() {
        return args -> {
            log.info("=== Starting test data generation ===");

            // Проверяем, нужно ли генерировать данные
            long userCount = userRepository.count();
            if (userCount > 100) {
                log.info("Database already contains {} users. Skipping generation.", userCount);
                return;
            }

            // Генерируем данные
            List<User> users = generateUsers(100);
            List<Card> cards = generateCards(users, 800);
            generateTransfers(cards, 100);

            log.info("=== Test data generation completed ===");
            log.info("Generated: {} users, {} cards, {} transfers", users.size(), cards.size(), 100);
        };
    }

    private List<User> generateUsers(int count) {
        log.info("Generating {} users...", count);
        List<User> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String email = String.format("user%d@test.com", i);
            String hashedPassword = passwordEncoder.encode("password123");

            User user = new User(new Email(email), new Password(hashedPassword), Role.USER);

            users.add(userRepository.save(user));

            if ((i + 1) % 20 == 0) {
                log.info("Generated {}/{} users", i + 1, count);
            }
        }

        return users;
    }

    private List<Card> generateCards(List<User> users, int count) {
        log.info("Generating {} cards...", count);
        List<Card> cards = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            User owner = users.get(random.nextInt(users.size()));

            CardNumber cardNumber = new CardNumber(generateValidCardNumber());
            CardExpiryDate expiryDate = CardExpiryDate.of(
                    2025 + random.nextInt(3), // 2025-2027
                    1 + random.nextInt(12) // 1-12
            );

            // Случайный баланс от 0 до 10000
            BigDecimal balance = BigDecimal.valueOf(random.nextDouble() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP);

            CardBalance cardBalance = new CardBalance(balance);

            // Случайный статус (80% ACTIVE, 15% BLOCKED, 5% PENDING)
            CardStatus status = getRandomStatus();

            Card card = Card.of(cardNumber, owner, expiryDate, status, cardBalance, cardEncryption);

            cards.add(cardRepository.save(card));

            if ((i + 1) % 100 == 0) {
                log.info("Generated {}/{} cards", i + 1, count);
            }
        }

        return cards;
    }

    private void generateTransfers(List<Card> cards, int count) {
        log.info("Generating {} transfers...", count);

        // Фильтруем только активные карты
        List<Card> activeCards = cards.stream().filter(Card::isActive).toList();

        if (activeCards.size() < 2) {
            log.warn("Not enough active cards to generate transfers");
            return;
        }

        int generated = 0;
        int attempts = 0;
        int maxAttempts = count * 10; // Максимум попыток

        while (generated < count && attempts < maxAttempts) {
            attempts++;

            try {
                // Выбираем случайного владельца
                Card fromCard = activeCards.get(random.nextInt(activeCards.size()));
                User owner = fromCard.getOwner();

                // Ищем другую карту того же владельца
                List<Card> ownerCards = activeCards.stream().filter(
                        c -> c.getOwner().equals(owner) && !c.equals(fromCard)).toList();

                if (ownerCards.isEmpty()) {
                    continue;
                }

                Card toCard = ownerCards.get(random.nextInt(ownerCards.size()));

                // Генерируем сумму (от 1 до баланса карты отправителя)
                BigDecimal maxAmount = fromCard.getBalance().getValue();
                if (maxAmount.compareTo(BigDecimal.ONE) <= 0) {
                    continue;
                }

                BigDecimal transferAmount = BigDecimal.valueOf(
                        1 + random.nextDouble() * Math.min(maxAmount.doubleValue() - 1, 1000)).setScale(
                                2,
                                BigDecimal.ROUND_HALF_UP);

                Amount amount = new Amount(transferAmount);

                // Выполняем перевод
                fromCard.subtractBalance(amount);
                toCard.addBalance(amount);

                Transfer transfer = Transfer.of(owner, fromCard, toCard, amount);
                transferRepository.save(transfer);

                generated++;

                if (generated % 20 == 0) {
                    log.info("Generated {}/{} transfers", generated, count);
                }

            } catch (Exception e) {
                // Игнорируем ошибки и пробуем снова
                continue;
            }
        }

        log.info("Successfully generated {} transfers (attempted {})", generated, attempts);
    }

    private CardStatus getRandomStatus() {
        int rand = random.nextInt(100);
        if (rand < 80) {
            return CardStatus.ACTIVE;
        } else if (rand < 95) {
            return CardStatus.BLOCKED;
        } else {
            return CardStatus.PENDING_ACTIVATION;
        }
    }

    private String generateValidCardNumber() {
        // Генерируем первые 15 цифр
        StringBuilder cardNumber = new StringBuilder();

        // BIN (первые 6 цифр) - используем популярные префиксы
        String[] binPrefixes = { "453201", "424242", "555555", "411111", "378282" };
        cardNumber.append(binPrefixes[random.nextInt(binPrefixes.length)]);

        // Следующие 9 цифр - случайные
        for (int i = 0; i < 9; i++) {
            cardNumber.append(random.nextInt(10));
        }

        // Вычисляем контрольную цифру по алгоритму Луна
        int checksum = calculateLuhnChecksum(cardNumber.toString());
        cardNumber.append(checksum);

        return cardNumber.toString();
    }

    private int calculateLuhnChecksum(String cardNumberWithout15) {
        int sum = 0;
        boolean isEvenPosition = true; // Начинаем с позиции, которая будет чётной после добавления контрольной цифры

        for (int i = cardNumberWithout15.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumberWithout15.charAt(i));

            if (isEvenPosition) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            isEvenPosition = !isEvenPosition;
        }

        return (10 - (sum % 10)) % 10;
    }
}
