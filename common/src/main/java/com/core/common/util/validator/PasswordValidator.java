package com.core.common.util.validator;

import com.core.common.util.validator.annotation.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private Pattern pattern;
    private Matcher matcher;

/*    (			# Start of group
            (?=.*\d)		#   must contains one digit from 0-9
            (?=.*[a-z])		#   must contains one lowercase characters
            (?=.*[A-Z])		#   must contains one uppercase characters
            (?=.*[@#$%])		#   must contains one special symbols in the list "@#$%"
            .		#     match anything with previous condition checking
    {6,20}	#        length at least 6 characters and maximum of 20
            )			# End of group*/

    //private static final String PASSWORD_PATTERN ="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    //private static final String PASSWORD_PATTERN ="((?=.*\\d).{6,20})";


    /*^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$

    ^                 # start-of-string
    (?=.*[0-9])       # a digit must occur at least once
    (?=.*[a-z])       # a lower case letter must occur at least once
    (?=.*[A-Z])       # an upper case letter must occur at least once
    (?=.*[@#$%^&+=])  # a special character must occur at least once
    (?=\S+$)          # no whitespace allowed in the entire string
    .{8,}             # anything, at least eight places though
    $                 # end-of-string*/

    //private static final String PASSWORD_PATTERN ="^(?=.*[A-Za-z@#$&^%]*)[A-Za-z@#$&^%\\d]{6,20}$";
    private static final String PASSWORD_PATTERN ="^(?=.*[A-Za-z@#$&^%+=]*)[A-Za-z@#$&^+=%\\d](?=\\S+$).{5,20}$";


    public void initialize(Password constraintAnnotation) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
