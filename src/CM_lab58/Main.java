package CM_lab58;

import java.util.ArrayList;
import java.util.function.Function;

import UTFE.TableOutput.Table;

public class Main {
    enum DEBUG {
        PRINT,
        NO
    }

    public static String getTableOfIntegration(double a, double b, Function<Double, Double> func,
                                               int countOfStep, double coeff, double precision, DEBUG debug) {

        ArrayList<Object[]> table = new ArrayList<>();
        table.add(new Object[]{"Номер\nитерации K", "Число\nотрезков\nразбиения N",
                "Шаг H", "Значение\nинтеграла I(k)", "|I(k) - I(k-1)|"});

        double prev = numIntegration(a, b, func, countOfStep, debug);

        if (debug == DEBUG.PRINT) System.out.println("Iteration " + 1 + ":");
        table.add(new Object[]{1, countOfStep, (b - a) / countOfStep, prev, "-"});

        for (int k = 2; true; ++k) {
            countOfStep = (int) Math.round(countOfStep * coeff);

            double I = numIntegration(a, b, func, countOfStep, debug);
            double eps = Math.abs(I - prev);
            prev = I;

            table.add(new Object[]{k, countOfStep, (b - a) / countOfStep, I, eps});

            if (eps <= precision) break;
        }


        return Table.TableToString(table.toArray(Object[][]::new));
    }

    public static double numIntegration(double a, double b, Function<Double, Double> func, int countOfSteps, DEBUG debug) {
        return numIntegration(a, b, func, (b - a) / countOfSteps, debug);
    }

    public static double numIntegration(double a, double b, Function<Double, Double> func, double step, DEBUG debug) {

        ArrayList<Double> fxList = new ArrayList<>();

        ArrayList<Object[]> table = new ArrayList<>();
        table.add(new Object[]{"i", "Xi", "F(Xi)"});

        int i = 0;

        for (double x = a; x <= b + step / 2; x += step) {
            double f = func.apply(x);
            fxList.add(f);
            table.add(new Object[]{i, x, f});
            i++;
        }

        double sum = fxList.stream().limit(fxList.size() - 1).mapToDouble(x -> x).sum() * step;

        if (debug == DEBUG.PRINT) {

            System.out.println("Integration from " + a + " to " + b + " with step = " + step);
            System.out.println(Table.TableToString(table.toArray(Object[][]::new)));

            StringBuilder sb = new StringBuilder("I = " + step + "(");
            for (i = 0; i < fxList.size() - 1; ++i) {
                if (i != 0) sb.append(" + ");
                sb.append(String.format("%.3f", fxList.get(i)));
            }
            sb.append("0) = ").append(sum);
            System.out.println(sb);
        }

        return sum;
    }

    public static void main(String[] args) {
        Table.SetDecimalPlaces(9);
        System.out.println(
                getTableOfIntegration(0, 1, x -> x * Math.exp(x), 10, 2, Math.pow(10, -7), DEBUG.NO)
        );
    }
}
