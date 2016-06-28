package com.andrewhale.usermanager;

import org.junit.Test;

import java.security.SecureRandom;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by andrew on 6/23/2016.
 */
public class ToolsTest {
    @Test
    public void testValidateEmailAddress() throws Exception {
        System.out.println("testValidateEmailAddress");

        // Good
        assertThat(Tools.validateEmailAddress("test@test.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("email@domain.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("firstname.lastname@domain.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("email@subdomain.domain.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("firstname+lastname@domain.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("email@[123.123.123.123]").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("“email”@domain.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("“1234567890@domain.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("“email@domain-one.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("“_______@domain.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("“email@domain.name").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("“email@domain.co.jp").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("“firstname-lastname@domain.com").isGood()).isTrue();
        assertThat(Tools.validateEmailAddress("あいうえお@domain.com").isGood()).isTrue();
        // 64 char local part
        assertThat(Tools.validateEmailAddress("1234567890123456789012345678901234567890123456789012345678901234@test.com").isGood()).isTrue();

        // Bad

        // 65 char local part
        assertThat(Tools.validateEmailAddress(null).getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("12345678901234567890123456789012345678901234567890123456789012345@test.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email@192.168.11.80").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("plainaddress").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("#@%^%#$@#$@#.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("@domain.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("Joe Smith <email@domain.com>").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email.domain.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email@domain@domain.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress(".email@domain.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email.@domain.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email..email@domain.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email@domain.com (Joe Smith)").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email@domain").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email@-domain.com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email@domain.web").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email@111.222.333.44444").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);
        assertThat(Tools.validateEmailAddress("email@domain..com").getErrorCode()).isEqualTo(Err.INVALID_EMAIL);

    }

    @Test
    public void testValidatePasswordRequirements() throws Exception {
        System.out.println("testValidatePasswordRequirements");
        assertThat(Tools.validatePasswordRequirements("1234567890").isGood()).isTrue();
        assertThat(Tools.validatePasswordRequirements("1234567890123456789012345678901234567890123456789012345678901234").isGood()).isTrue();
        assertThat(Tools.validatePasswordRequirements("あいうえおあいうえお").isGood()).isTrue();

        assertThat(Tools.validatePasswordRequirements("123456789").getErrorCode()).isEqualTo(Err.INVALID_PASS);
        assertThat(Tools.validatePasswordRequirements("12345678901234567890123456789012345678901234567890123456789012345").getErrorCode()).isEqualTo(Err.INVALID_PASS);
        assertThat(Tools.validatePasswordRequirements(null).getErrorCode()).isEqualTo(Err.INVALID_PASS);

    }

    @Test
    public void testHashPassword() throws Exception {
        System.out.println("testHashPassword");
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[256];
        random.nextBytes(salt);

        byte[] hashed = Tools.hashPassword("password".toCharArray(), salt, 10, 256);
        assertThat(hashed).isEqualTo(Tools.hashPassword("password".toCharArray(), salt, 10, 256));

        hashed = Tools.hashPassword("1234567890123456789012345678901234567890123456789012345678901234".toCharArray(), salt, 10, 256);
        assertThat(hashed).isEqualTo(Tools.hashPassword("1234567890123456789012345678901234567890123456789012345678901234".toCharArray(), salt, 10, 256));

        hashed = Tools.hashPassword("あいうえおあいうえお".toCharArray(), salt, 10, 256);
        assertThat(hashed).isEqualTo(Tools.hashPassword("あいうえおあいうえお".toCharArray(), salt, 10, 256));

        hashed = Tools.hashPassword("".toCharArray(), salt, 10, 256);
        assertThat(hashed).isEqualTo(Tools.hashPassword("".toCharArray(), salt, 10, 256));
    }

}