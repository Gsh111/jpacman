package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Class for testing the different paths in a single level game.
 */
public class GameTest {

    private Launcher launcher;

    /**
     * Launch the game played.
     *
     * @param mapName the name of the map file
     */
    public void init(String[] mapName) {
        launcher = new Launcher().withMapFile(mapName[0]);
    }

    /**
     * Set a new launcher.
     *
     * @param launcher the launcher to be set
     */
    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    /**
     * Return the launcher for the test.
     *
     * @return the launcher of the test
     */
    public Launcher getLauncher() {
        return launcher;
    }

    /**
     * Test for consuming all of the pellets from all levels and thus winning the game.
     * 消耗关卡中的所有小球，赢得游戏
     */
    @Test
    @DisplayName("测试消耗关卡中的所有小球，赢得游戏")
    void testWin() {
        String[] map = {"/mapTest.txt"};
        init(map);
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);
        //launcher = new Launcher().withMapFile("/mapTest.txt");

        launcher.launch();
        Game game = launcher.getGame();
        game.getLevel().addObserver(levelObserver);
        assertThat(game.isInProgress()).isFalse();

        game.start();
        Player myPlayer = game.getPlayers().get(0);

        game.move(myPlayer, Direction.EAST);
        game.move(myPlayer, Direction.EAST);
        game.move(myPlayer, Direction.SOUTH);
        game.move(myPlayer, Direction.WEST);
        game.move(myPlayer, Direction.WEST);
        game.move(myPlayer, Direction.SOUTH);
        game.move(myPlayer, Direction.EAST);

        assertThat(myPlayer.isAlive()).isTrue();
        Mockito.verify(levelObserver, Mockito.times(1)).levelWon();
        assertThat(game.isInProgress()).isFalse();

        game.stop();
    }


    /**
     * Test for consuming one of the pellets(not the last one).
     */
    @Test
    @DisplayName("测试消耗了一个豆子")
    void testConsumePellet() {
        String[] map = {"/mapTest.txt"};
        init(map);

        launcher.launch();
        final int score = 20;

        Game game = launcher.getGame();
        assertThat(game.isInProgress()).isFalse();

        game.start();
        Player myPlayer = game.getPlayers().get(0);

        game.move(myPlayer, Direction.EAST);
        game.move(myPlayer, Direction.SOUTH);

        assertThat(myPlayer.getScore()).isEqualTo(score);
        assertThat(game.isInProgress()).isTrue();

        game.stop();
    }

    /**
     * Test for collision with a ghost and thus losing the game.
     */
    @Test
    @DisplayName("测试吃豆人碰到魔鬼")
    void testLose() {
        String[] map = {"/mapLose.txt"};
        init(map);
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);


        launcher.launch();
        Game game = launcher.getGame();
        game.getLevel().addObserver(levelObserver);
        assertThat(game.isInProgress()).isFalse();

        game.start();
        Player player = game.getPlayers().get(0);

        game.move(player, Direction.SOUTH);

        assertThat(player.isAlive()).isFalse();
        Mockito.verify(levelObserver, Mockito.times(1)).levelLost();
        assertThat(game.isInProgress()).isFalse();

        game.stop();
    }

    /**
     * Test for moving to an empty cell and thus not changing the state of the game.
     */
    @Test
    @DisplayName("测试移动到一个空单元格，从而不改变游戏状态")
    void testMoveEmpty() {
        String[] map = {"/moveEmpty.txt"};
        init(map);

        launcher.launch();
        Game game = launcher.getGame();
        assertThat(game.isInProgress()).isFalse();

        game.start();
        Player player = game.getPlayers().get(0);

        game.move(player, Direction.EAST);

        assertThat(player.isAlive()).isTrue();
        assertThat(player.getScore()).isEqualTo(0);
        assertThat(game.isInProgress()).isTrue();

        game.stop();
    }

    /**
     * Test for moving towards a wall and thus staying in the same square.
     */
    @Test
    @DisplayName("测试移动到墙壁，不能再进行移动")
    void testMoveWall() {
        String[] map = {"/moveWall.txt"};
        init(map);


        launcher.launch();
        Game game = launcher.getGame();
        assertThat(game.isInProgress()).isFalse();

        game.start();
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        game.move(player, Direction.NORTH);

        assertThat(player.getSquare()).isEqualTo(square);
        assertThat(player.isAlive()).isTrue();
        assertThat(player.getScore()).isEqualTo(0);
        assertThat(game.isInProgress()).isTrue();

        game.stop();
    }

    /**
     * Test for stopping and resuming the game.
     */
    @Test
    @DisplayName("测试停止游戏")
    void testStopStart() {
        String[] map = {"/sampleMap.txt"};
        init(map);


        launcher.launch();
        Game game = launcher.getGame();
        assertThat(game.isInProgress()).isFalse();

        game.start();
        assertThat(game.isInProgress()).isTrue();

        game.stop();
        assertThat(game.isInProgress()).isFalse();

        game.start();
        assertThat(game.isInProgress()).isTrue();

        game.stop();
    }

    /**
     * Test when game is not started and you want to move towards a wall.
     */
    @Test
    @DisplayName("测试游戏未开始时，吃豆人移动到墙壁")
    void testNotStartedMoveWall() {
        String[] map = {"/moveWall.txt"};
        init(map);

        launcher.launch();

        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        assertThat(game.isInProgress()).isFalse();

        game.move(player, Direction.NORTH);
        assertThat(player.getSquare()).isEqualTo(square);

        assertThat(game.isInProgress()).isFalse();
    }


    /**
     * Test when game is not started and you want to move to an empty square.
     */
    @Test
    @DisplayName("测试游戏未开始时，吃豆人移动到空格子")
    void testNotStartedMoveEmpty() {
        String[] map = {"/moveEmpty.txt"};
        init(map);

        launcher.launch();

        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        assertThat(game.isInProgress()).isFalse();

        game.move(player, Direction.EAST);
        assertThat(game.isInProgress()).isFalse();
    }


    /**
     * Test when the game is not started and you want to move towards a pellet.
     */
    @Test
    @DisplayName("测试游戏未开始时，吃豆人移动吃豆子")
    void testNotStartedMovePellet() {
        String[] map = {"/mapTest.txt"};
        init(map);
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);

        launcher.launch();

        Game game = launcher.getGame();
        game.getLevel().addObserver(levelObserver);
        Player player = game.getPlayers().get(0);

        assertThat(game.isInProgress()).isFalse();

        game.move(player, Direction.EAST);

        assertThat(game.isInProgress()).isFalse();
        assertThat(player.getScore()).isEqualTo(0);
    }


    /**
     * Test when the game is stopped and you want to move towards a wall.
     */
    @Test
    @DisplayName("测试游戏被停止时，吃豆人移动到墙壁")
    void testSuspendMoveWall() {
        String[] map = {"/moveWall.txt"};
        init(map);

        launcher.launch();

        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        game.start();
        assertThat(game.isInProgress()).isTrue();

        game.stop();
        assertThat(game.isInProgress()).isFalse();

        game.move(player, Direction.NORTH);

        assertThat(game.isInProgress()).isFalse();
    }


    /**
     * Test when the game is stopped and you want to move to an empty box.
     */
    @Test
    @DisplayName("测试游戏被停止时，吃豆人移动到空格子")
    void testSuspendMoveEmpty() {
        String[] map = {"/moveEmpty.txt"};
        init(map);

        launcher.launch();

        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        game.start();
        assertThat(game.isInProgress()).isTrue();

        game.stop();
        assertThat(game.isInProgress()).isFalse();

        game.move(player, Direction.EAST);
        assertThat(player.getSquare()).isEqualTo(square);

        assertThat(game.isInProgress()).isFalse();
    }


    /**
     * Test when the game is started and you press start again.
     */
    @Test
    @DisplayName("测试游戏被停止时，再次开始")
    void testInGameStart() {
        String[] map = {"/mapTest.txt"};
        init(map);
        //launcher = new Launcher().withMapFile("/mapTest.txt");
        launcher.launch();

        Game game = launcher.getGame();
        game.start();
        assertThat(game.isInProgress()).isTrue();

        game.start();
        assertThat(game.isInProgress()).isTrue();
    }

    /**
     * Test when the game is won and you try to start it again.
     */
    @Test
    @DisplayName("测试赢得游戏后，再次开始游戏")
    void testWinStart() {
        String[] map = {"/mapLose.txt"};
        init(map);
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);
        //launcher = new Launcher().withMapFile("/mapLose.txt");
        launcher.launch();

        Game game = launcher.getGame();
        game.getLevel().addObserver(levelObserver);
        game.start();
        Player player = game.getPlayers().get(0);

        game.move(player, Direction.EAST);
        Mockito.verify(levelObserver, Mockito.times(1)).levelWon();

        game.start();
        assertThat(game.isInProgress()).isFalse();
    }


    /**
     * Test when the game is lost and you try to start it again.
     */
    @Test
    @DisplayName("测试游戏出现故障退出，再次开始游戏")
    void testLoseStart() {
        String[] map = {"/mapLose.txt"};
        init(map);
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);
        //launcher = new Launcher().withMapFile("/mapLose.txt");
        launcher.launch();

        Game game = launcher.getGame();
        game.getLevel().addObserver(levelObserver);
        game.start();
        Player player = game.getPlayers().get(0);

        game.move(player, Direction.SOUTH);
        Mockito.verify(levelObserver, Mockito.times(1)).levelLost();

        game.start();
        assertThat(game.isInProgress()).isFalse();
    }
}
