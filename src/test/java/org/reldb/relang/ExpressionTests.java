package org.reldb.relang;

import org.junit.jupiter.api.Test;
import org.reldb.relang.helper.LanguageTests;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExpressionTests extends LanguageTests {
    @Test
    void simpleExpression() throws Exception {
        var source = """
                RETURN 3
                """;

        var result = evaluate(source);

        assertThat(result).isInstanceOf(Long.class);
        assertThat(result.toString()).isEqualTo("3");
    }

    @Test
    void simpleStatementAndExpression() throws Exception {
        var source = """
                a = 2 + 3
                RETURN a
                """;

        var result = evaluate(source);

        assertThat(result).isInstanceOf(Long.class);
        assertThat(result.toString()).isEqualTo("5");
    }
}
