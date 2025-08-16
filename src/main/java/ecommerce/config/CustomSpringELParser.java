package ecommerce.config;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Spring Expression Language (SpEL) 커스텀 파서 클래스
 * 전달받은 Lock의 이름을 SpEl 로 파싱해서 읽어옴
 * */
public class CustomSpringELParser {

    private CustomSpringELParser () {
        // private 생성자: 인스턴스 생성을 방지
    }

    public static Object getDynamicValue (String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
    }

}
