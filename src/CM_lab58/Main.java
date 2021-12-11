package CM_lab58;

import java.util.ArrayList;
import java.util.function.Function;

import UTFE.TableOutput.Table;

public class Main {

    enum DEBUG {
        PRINT,
        NO
    }

    enum TYPE {
        LEFT_RECT,
        RIGHT_RECT,
        MID_RECT,
        TRAPEZ,
        SIMSPON
    }

    public static String getTableOfIntegration(double a, double b, Function<Double, Double> func,
                                               int countOfStep, double coeff, double precision, TYPE type, DEBUG debug) {

        ArrayList<Object[]> table = new ArrayList<>();
        table.add(new Object[]{"Номер\nитерации K", "Число\nотрезков\nразбиения N",
                "Шаг H", "Значение\nинтеграла I(k)", "|I(k) - I(k-1)|"});

        double prev = numIntegration(a, b, func, countOfStep, type, debug);

        if (debug == DEBUG.PRINT) System.out.println("Iteration " + 1 + ":");
        table.add(new Object[]{1, countOfStep, (b - a) / countOfStep, prev, "-"});

        for (int k = 2; true; ++k) {
            countOfStep = (int) Math.round(countOfStep * coeff);

            double I = numIntegration(a, b, func, countOfStep, type, debug);
            double eps = Math.abs(I - prev);
            prev = I;

            table.add(new Object[]{k, countOfStep, (b - a) / countOfStep, I, eps});

            if (eps <= precision) break;
        }

        return Table.TableToString(table.toArray(Object[][]::new));
    }

    public static double numIntegration(double a, double b, Function<Double, Double> func, int countOfSteps, TYPE type, DEBUG debug) {
        return numIntegration(a, b, func, (b - a) / countOfSteps, type, debug);
    }

    public static double numIntegration(double a, double b, Function<Double, Double> func, double step, TYPE type, DEBUG debug) {

        ArrayList<Double> fxList = new ArrayList<>();

        ArrayList<Object[]> table = new ArrayList<>();
        table.add(new Object[]{"i", "Xi", "F(Xi)"});

        int i = 0;

        if (type == TYPE.MID_RECT) a += step / 2;

        for (double x = a; x <= b + step / 3; x += step) {
            double f = func.apply(x);
            fxList.add(f);
            table.add(new Object[]{i, x, f});
            i++;
        }

        if (type == TYPE.MID_RECT) a -= step / 2;


        double sum = 0;
        switch (type) {
            case LEFT_RECT -> {
                sum = fxList.stream().limit(fxList.size() - 1).mapToDouble(x -> x).sum() * step;
            }
            case RIGHT_RECT -> {
                sum = fxList.stream().skip(1).mapToDouble(x -> x).sum() * step;
            }
            case MID_RECT -> {
                sum = fxList.stream().mapToDouble(x -> x).sum() * step;
            }
            case TRAPEZ -> {
                sum = (fxList.stream().limit(fxList.size() - 1).skip(1).mapToDouble(x -> x).sum() * 2
                        + fxList.get(0) + fxList.get(fxList.size() - 1)) * step / 2;
            }
            case SIMSPON -> {
                double sum1 = 0;
                double sum2 = 0;
                for (i = 1; i < fxList.size() - 1; ++i) {
                    if (i % 2 == 0) sum2 += fxList.get(i);
                    else sum1 += fxList.get(i);
                }
                sum = (4 * sum1 + 2 * sum2 + fxList.get(0) + fxList.get(fxList.size() - 1)) * step / 3;
            }
        }

        if (debug == DEBUG.PRINT) {

            System.out.println("Integration from " + a + " to " + b + " with step = " + step);
            System.out.println(Table.TableToString(table.toArray(Object[][]::new)));

            StringBuilder sb = new StringBuilder("I = " + step + "(");

            int dgt = 3;

            int left = type == TYPE.RIGHT_RECT || type == TYPE.TRAPEZ ? 1 : 0;
            int right = type == TYPE.LEFT_RECT || type == TYPE.TRAPEZ ? fxList.size() - 1 : fxList.size();

            if (type == TYPE.TRAPEZ) {
                sb = new StringBuilder("I = (" + step + "/" + "2)*(");
            }

            if (type == TYPE.SIMSPON) {
                sb = new StringBuilder("I = (" + step + "/" + "3)*(");
                sb
                        .append(String.format("%." + dgt + "f", fxList.get(0)))
                        .append(" + ")
                        .append(String.format("%." + dgt + "f", fxList.get(fxList.size() - 1)))
                        .append(" + 4(");

                for (i = 1; i < fxList.size() - 1; ++i) {
                    if (i % 2 == 1) {
                        if (i != 1) sb.append(" + ");
                        sb.append(String.format("%." + dgt + "f", fxList.get(i)));

                    }
                }
                sb.append(") + 2(");
                for (i = 1; i < fxList.size() - 1; ++i) {
                    if (i % 2 == 0) {
                        if (i != 2) sb.append(" + ");
                        sb.append(String.format("%." + dgt + "f", fxList.get(i)));
                    }
                }
                sb.append(")");
            } else {
                for (i = left; i < right; ++i) {
                    if (i != left) sb.append(" + ");
                    sb.append(String.format("%." + dgt + "f", fxList.get(i)));
                }
            }
            sb.append(") = ").append(sum);
            System.out.println(sb);
        }

        return sum;
    }

    public static void main(String[] args) {
        Table.SetDecimalPlaces(9);
        for (int i = 0; i < TYPE.values().length; ++i){
            System.out.print(("=".repeat(136) + "\n").repeat(3));
            System.out.println("Method type: " + TYPE.values()[i]);
            System.out.println(
                    getTableOfIntegration(0, 1, x -> x * Math.exp(x),
                            10, 2, Math.pow(10, -3), TYPE.values()[i], DEBUG.NO)
            );
        }

    }
}
