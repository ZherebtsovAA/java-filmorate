package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ValidReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private String message;

    @Override
    public void initialize(final ValidReleaseDate constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final LocalDate releaseDate, final ConstraintValidatorContext constraintValidatorContext) {
        LocalDate birthdayCinema = LocalDate.of(1895, 12, 28);
        if (releaseDate != null) {
            return releaseDate.isAfter(birthdayCinema);
        }
        return false;
    }
}
