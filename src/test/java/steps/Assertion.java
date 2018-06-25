package steps;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Assertion {

    private Boolean pass;
    private String message;

    static Assertion createAssertion(Boolean pass, String message) {
        if(pass) {
            return new Assertion(pass, "");
        } else {
            return new Assertion(false, message);
        }
    }
}
