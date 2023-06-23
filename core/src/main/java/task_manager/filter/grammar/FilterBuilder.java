package task_manager.filter.grammar;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import task_manager.filter.*;

public class FilterBuilder {
    public static FilterCriterion buildFilter(String query) {
        QueryLexer lexer = new QueryLexer(CharStreams.fromString(query));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QueryParser parser = new QueryParser(tokens);

        // Parse the input into an abstract syntax tree (AST)
        ParseTree tree = parser.query();

        // Traverse the AST and build a custom object hierarchy
        return buildFilter(tree);
    }

    private static FilterCriterion buildFilter(ParseTree tree) {
        return buildFilterCriterion(((QueryParser.QueryContext) tree).expr());
    }

    private static FilterCriterion buildFilterCriterion(ParseTree tree) {
        if (tree instanceof QueryParser.ExprContext expr) {
            if (expr.OPEN_PAREN() != null) {
                return buildFilterCriterion(expr.expr(0));
            } else if (expr.OR() != null) {
                FilterCriterion criterion1 = buildFilterCriterion(expr.expr(0));
                FilterCriterion criterion2 = buildFilterCriterion(expr.expr(1));
                return new OrFilterCriterion(criterion1, criterion2);
            } else if (expr.AND() != null) {
                FilterCriterion criterion1 = buildFilterCriterion(expr.expr(0));
                FilterCriterion criterion2 = buildFilterCriterion(expr.expr(1));
                return new AndFilterCriterion(criterion1, criterion2);
            } else if (expr.equalsExpr() != null) {
                return buildFilterCriterion(expr.equalsExpr());
            } else if (expr.likeExpr() != null) {
                return buildFilterCriterion(expr.likeExpr());
            }
        } else if (tree instanceof QueryParser.EqualsExprContext) {
            String propertyName = ((QueryParser.EqualsExprContext) tree).PROPERTY_NAME().getText();

            QueryParser.ConstantContext constant =
                ((QueryParser.EqualsExprContext) tree).constant();
            if (constant.string() != null) {
                String propertyValue = constant.string().SINGLE_STRING() != null
                    ? constant.string().SINGLE_STRING().getText()
                    : constant.string().DOUBLE_STRING().getText();
                return new EqualFilterCriterion(propertyName,
                        propertyValue.substring(1, propertyValue.length() - 1));
            } else if (constant.bool() != null) {
                QueryParser.BoolContext bool = constant.bool();
                if (bool.TRUE() != null) {
                    return new EqualFilterCriterion(propertyName, true);
                } else if (bool.FALSE() != null) {
                    return new EqualFilterCriterion(propertyName, false);
                }
            }
        } else if (tree instanceof QueryParser.LikeExprContext) {
            String propertyName = ((QueryParser.LikeExprContext) tree).PROPERTY_NAME().getText();
            QueryParser.StringContext string = ((QueryParser.LikeExprContext) tree).string();
            String propertyValue = string.SINGLE_STRING() != null
                ? string.SINGLE_STRING().getText()
                : string.DOUBLE_STRING().getText();
            return new ContainsCaseInsensitiveFilterCriterion(propertyName,
                propertyValue.substring(1, propertyValue.length() - 1));
        }

        throw new RuntimeException();
    }
}
