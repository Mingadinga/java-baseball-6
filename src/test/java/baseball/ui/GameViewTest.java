package baseball.ui;

import static org.assertj.core.api.Assertions.assertThat;

import baseball.controller.AbstractGameController;
import baseball.controller.GameController;
import baseball.state.Scoring;
import baseball.ui.output.component.OutputComponentImpl;
import baseball.ui.output.format.ResultFormatStringCreatorImpl;
import baseball.util.DefaultRandomNumberCreatorByDigit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameViewTest {

    final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    final PrintStream standardOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    public void 게임시작_시작안내문구를_출력한다() {
        AtomicInteger count = new AtomicInteger(0);
        GameController mockGameControllerOnce = getGameController(() -> count.getAndIncrement() < 1);
        GameView gameView = getGameView(mockGameControllerOnce);

        gameView.start();

        assertExpectedStringIsContainedInTargetString("숫자 야구 게임을 시작합니다.", outputStreamCaptor.toString().trim());

    }

    @Test
    public void 추측시작_입력안내문구를_출력한다() {
        AtomicInteger count = new AtomicInteger(0);
        GameController mockGameControllerOnce = getGameController(() -> count.getAndIncrement() < 1);
        GameView gameView = getGameView(mockGameControllerOnce);

        gameView.guessUntilCorrect();

        assertExpectedStringIsContainedInTargetString("숫자를 입력해주세요 : ", outputStreamCaptor.toString().trim());
    }

    @Test
    public void 추측_실패시_다시_추측한다() {
        AtomicInteger count = new AtomicInteger(0);
        GameController mockGameControllerTerminateAfterTwice = getGameController(() -> count.getAndIncrement() < 2);
        GameView gameView = getGameView(mockGameControllerTerminateAfterTwice);

        gameView.guessUntilCorrect();

        assertExpectedStringIsContainedInTargetString("숫자를 입력해주세요 : 숫자를 입력해주세요 : ",
                outputStreamCaptor.toString().trim());
    }

    @Test
    public void 정답_후_재시작() {

    }

    @Test
    public void 정답_후_종료() {

    }

    @Test
    public void 추측_성공시_성공문구_출력한다() {
        GameController mockGameControllerTerminateInstantly = getGameController(() -> false);
        GameView gameView = getGameView(mockGameControllerTerminateInstantly);

        gameView.guessUntilCorrect();

        assertExpectedStringIsContainedInTargetString("3개의 숫자를 모두 맞히셨습니다! 게임 종료", outputStreamCaptor.toString().trim());
    }

    private void assertExpectedStringIsContainedInTargetString(String expectedString, String targetString) {
        assertThat(targetString.contains(expectedString));
    }

    private GameView getGameView(GameController gameController) {
        return new GameView(gameController,
                () -> "123", () -> "2",
                new OutputComponentImpl(), new ResultFormatStringCreatorImpl());
    }

    private static GameController getGameController(BooleanSupplier booleanSupplier) {
        GameController mockGameController = new AbstractGameController(
                getDefaultScoring()) {
            @Override
            public boolean isStillWrong() {
                return booleanSupplier.getAsBoolean();
            }
        };
        return mockGameController;
    }

    private static void mockInput() {
        String input = "123";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }

    private static Scoring getDefaultScoring() {
        return new Scoring(new DefaultRandomNumberCreatorByDigit().create(3));
    }

}