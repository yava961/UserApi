package UserApi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {

    @Value("${validation.minYears}")
    private int minYears;

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext constraintValidatorContext) {
        var now = LocalDate.now();
        var period = Period.between(birthDate, now);
        return period.getYears() >= minYears;
    }
}
