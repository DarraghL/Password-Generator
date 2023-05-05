import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordStrengthCalculatorControllerTest {
    private PasswordStrengthCalculatorController controller;

    @BeforeEach
    void setUp() {
        controller = new PasswordStrengthCalculatorController();
    }

    @Test
    void testCalculatePasswordStrength() {
        String weakPassword = "weak";
        List<String> weakReasons = controller.calculatePasswordStrength(weakPassword);
        assertFalse(weakReasons.isEmpty());

        String strongPassword = "S5tR0nGP@S$w0Rd_123!";
        List<String> strongReasons = controller.calculatePasswordStrength(strongPassword);
        assertTrue(strongReasons.isEmpty());
    }

    @Test
    void testLengthRequirement() {
        String shortPassword = "Sh0rt!";
        List<String> shortReasons = controller.calculatePasswordStrength(shortPassword);
        assertTrue(shortReasons.contains("Password is too short"));

        String longPassword = "L0ng3n0ughP@S$w0Rd_123!";
        List<String> longReasons = controller.calculatePasswordStrength(longPassword);
        assertFalse(longReasons.contains("Password is too short"));
    }

    @Test
    void testLowercaseRequirement() {
        String noLowercasePassword = "NOLOWERCASE1!";
        List<String> noLowercaseReasons = controller.calculatePasswordStrength(noLowercasePassword);
        assertTrue(noLowercaseReasons.contains("Password does not contain lowercase letters"));

        String hasLowercasePassword = "HasLowercase1!";
        List<String> hasLowercaseReasons = controller.calculatePasswordStrength(hasLowercasePassword);
        assertFalse(hasLowercaseReasons.contains("Password does not contain lowercase letters"));
    }

    @Test
    void testUppercaseRequirement() {
        String noUppercasePassword = "nouppercase1!";
        List<String> noUppercaseReasons = controller.calculatePasswordStrength(noUppercasePassword);
        assertTrue(noUppercaseReasons.contains("Password does not contain uppercase letters"));

        String hasUppercasePassword = "HasUppercase1!";
        List<String> hasUppercaseReasons = controller.calculatePasswordStrength(hasUppercasePassword);
        assertFalse(hasUppercaseReasons.contains("Password does not contain uppercase letters"));
    }

    @Test
    void testDigitRequirement() {
        String noDigitPassword = "NoDigit!";
        List<String> noDigitReasons = controller.calculatePasswordStrength(noDigitPassword);
        assertTrue(noDigitReasons.contains("Password does not contain digits"));

        String hasDigitPassword = "HasDigit1!";
        List<String> hasDigitReasons = controller.calculatePasswordStrength(hasDigitPassword);
        assertFalse(hasDigitReasons.contains("Password does not contain digits"));
    }

    @Test
    void testSpecialCharacterRequirement() {
        String noSpecialCharacterPassword = "NoSpecialCharacter1";
        List<String> noSpecialCharacterReasons = controller.calculatePasswordStrength(noSpecialCharacterPassword);
        assertTrue(noSpecialCharacterReasons.contains("Password does not contain special characters"));

        String hasSpecialCharacterPassword = "HasSpecialCharacter1!";
        List<String> hasSpecialCharacterReasons = controller.calculatePasswordStrength(hasSpecialCharacterPassword);
        assertFalse(hasSpecialCharacterReasons.contains("Password does not contain special characters"));
    }
}
