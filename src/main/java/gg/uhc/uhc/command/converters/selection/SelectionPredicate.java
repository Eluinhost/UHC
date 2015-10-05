package gg.uhc.uhc.command.converters.selection;

import com.google.common.base.Predicate;

public abstract class SelectionPredicate<T> implements Predicate<T> {
    public abstract String getTypeString();

    public static SelectionPredicate<Integer> ANY_INTEGER = new SelectionPredicate<Integer> (){
        @Override
        public String getTypeString() {
            return "Integer";
        }

        @Override
        public boolean apply(Integer input) {
            return true;
        }
    };

    public static SelectionPredicate<Integer> POSITIVE_INTEGER = new SelectionPredicate<Integer> (){
        @Override
        public String getTypeString() {
            return "Integer > 0";
        }

        @Override
        public boolean apply(Integer input) {
            return input > 0;
        }
    };

    public static SelectionPredicate<Integer> POSITIVE_INTEGER_INC_ZERO = new SelectionPredicate<Integer> (){
        @Override
        public String getTypeString() {
            return "Integer >= 0";
        }

        @Override
        public boolean apply(Integer input) {
            return input >= 0;
        }
    };

    public static SelectionPredicate<Double> ANY_DOUBLE = new SelectionPredicate<Double>() {
        @Override
        public String getTypeString() {
            return "Any Number";
        }

        @Override
        public boolean apply(Double input) {
            return true;
        }
    };

    public static SelectionPredicate<Double> POSITIVE_DOUBLE = new SelectionPredicate<Double>() {
        @Override
        public String getTypeString() {
            return "Number > 0";
        }

        @Override
        public boolean apply(Double input) {
            return input > 0;
        }
    };

    public static SelectionPredicate<Double> POSITIVE_DOUBLE_INC_ZERO = new SelectionPredicate<Double>() {
        @Override
        public String getTypeString() {
            return "Number >= 0";
        }

        @Override
        public boolean apply(Double input) {
            return input >= 0;
        }
    };
}
