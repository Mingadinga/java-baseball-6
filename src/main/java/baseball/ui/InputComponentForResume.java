package baseball.ui;

import camp.nextstep.edu.missionutils.Console;

public class InputComponentForResume implements InputComponent {

    private final InputValidator inputValidator;

    public InputComponentForResume(InputValidator inputValidator) {
        this.inputValidator = inputValidator;
    }

    @Override
    public String getValidUserInput() {
        return inputValidator.validateInput(Console.readLine());
    }
}
