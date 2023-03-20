package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Tests for a splendor game.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SplendorGameTests {

    final Logger logger = LoggerFactory.getLogger(SplendorGameTests.class);

    @Test
    void testGameWorks() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        assertThat(game.getTurnCounter()).isEqualTo(0);
    }

}
