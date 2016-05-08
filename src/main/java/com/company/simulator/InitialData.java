package com.company.simulator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by frox on 7.5.16.
 */
public class InitialData {
    static double[][] taxiPosArray = {{50.109203, 14.272379},{50.059044, 14.499396},{50.109203, 14.272379},{50.08688469308504, 14.440087974071503}, {50.089748306534894, 14.433736503124237}, {50.09095978379497, 14.431333243846893}, {50.08941789833057, 14.42841500043869}, {50.09095978379497, 14.42790001630783}, {50.09073951748018, 14.423265159130096}, {50.09007871246126, 14.419316947460175}, {50.08578325773694, 14.416055381298065}, {50.08336001087548, 14.415883719921112}, {50.07939443357569, 14.415540397167206}, {50.07939443357569, 14.417771995067596}, {50.07741152190898, 14.420346915721893}, {50.077742012882055, 14.424295127391815}, {50.08214834150433, 14.446439445018768}, {50.07487768209724, 14.456567466259003}, {50.071903003629785, 14.439058005809784}, {50.07124193891019, 14.416570365428925}, {50.067275359221334, 14.388761222362518}, {50.08358031110699, 14.369535148143768}, {50.09239149016361, 14.369191825389862}, {50.0960256296116, 14.364556968212128}, {50.09943926711292, 14.376058280467987}, {50.10241223708929, 14.410905539989471}, {50.10186170101348, 14.4316765666008}, {50.10516482256174, 14.447984397411346}, {50.1036233941846, 14.454164206981659}, {50.08996857740557, 14.42017525434494}, {50.08578325773694, 14.416227042675018}, {50.08358031110699, 14.404039084911346}, {50.072564059236534, 14.386872947216034}, {50.0716826497358, 14.370908439159393}, {50.067826292685275, 14.366273581981659}, {50.09393327995519, 14.457597434520721}, {50.08336001087548, 14.4536492228508}, {50.078843633005896, 14.456910789012909}, {50.078402987993854, 14.419145286083221}, {50.08380061032603, 14.41742867231369}, {50.08611369099903, 14.419316947460175}, {50.0839107595559, 14.42892998456955}, {50.08269910410617, 14.43116158246994}, {50.06220647441564, 14.393052756786346}, {50.06187587636551, 14.425496757030487}, {50.07212335651124, 14.438199698925018}, {50.08435135394416, 14.416570365428925}, {50.08170772686679, 14.3712517619133}};
    //static double[][] taxiPosArray = {{50.048604, 14.429829},{50.046606, 14.430194}};
    public static List<double[]> getTaxiPositions() {
        return Arrays.asList(taxiPosArray);
    }
}
