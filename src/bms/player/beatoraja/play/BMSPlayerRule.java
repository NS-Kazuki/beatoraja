package bms.player.beatoraja.play;

import bms.model.BMSModel;
import bms.model.Mode;

/**
 * プレイヤールール
 * 
 * @author exch
 */
public enum BMSPlayerRule {

    BEAT_5K(GaugeProperty.FIVEKEYS, JudgeProperty.FIVEKEYS),
    BEAT_7K(GaugeProperty.SEVENKEYS, JudgeProperty.SEVENKEYS),
    BEAT_10K(GaugeProperty.FIVEKEYS, JudgeProperty.FIVEKEYS),
    BEAT_14K(GaugeProperty.SEVENKEYS, JudgeProperty.SEVENKEYS),
    POPN_5K(GaugeProperty.PMS, JudgeProperty.PMS),
    POPN_9K(GaugeProperty.PMS, JudgeProperty.PMS),
    KEYBOARD_24K(GaugeProperty.KEYBOARD, JudgeProperty.KEYBOARD),
    KEYBOARD_24K_DOUBLE(GaugeProperty.KEYBOARD, JudgeProperty.KEYBOARD),
    LR2(GaugeProperty.LR2, JudgeProperty.SEVENKEYS),
    Default(GaugeProperty.SEVENKEYS, JudgeProperty.SEVENKEYS),
    ;

    public final GaugeProperty gauge;
    public final JudgeProperty judge;

    private BMSPlayerRule(GaugeProperty gauge, JudgeProperty judge) {
        this.gauge = gauge;
        this.judge = judge;
    }

    public static BMSPlayerRule getBMSPlayerRule(Mode mode) {
        for(BMSPlayerRule bmsrule : BMSPlayerRule.values()) {
            if(bmsrule.name().equals(mode.name())) {
                return bmsrule;
            }
        }
        return Default;
    }
    
    public static void validate(BMSModel model, boolean bmson) {
    	BMSPlayerRule rule = getBMSPlayerRule(model.getMode());
    	int judgerank = model.getJudgerank();
		if (judgerank >= 0 && model.getJudgerank() < 5) {
			model.setJudgerank(rule.judge.windowrule.judgerank[judgerank]);
		} else if (model.getJudgerank() < 0) {
			model.setJudgerank(100);
		}
		
		if(bmson) {
			// TOTAL未定義の場合
			if (model.getTotal() <= 0.0) {
				model.setTotal(100.0);
			}			
			model.setTotal(model.getTotal() / 100.0 * calculateDefaultTotal(model.getMode(), model.getTotalNotes()));
		} else {
			// TOTAL未定義の場合
			if (model.getTotal() <= 0.0) {
				model.setTotal(calculateDefaultTotal(model.getMode(), model.getTotalNotes()));
			}			
		}
    }
    
	private static double calculateDefaultTotal(Mode mode, int totalnotes) {
		switch (mode) {
		case BEAT_7K:
		case BEAT_5K:
		case BEAT_14K:
		case BEAT_10K:
		case POPN_9K:
		case POPN_5K:
			return Math.max(260.0, 7.605 * totalnotes / (0.01 * totalnotes + 6.5));
		case KEYBOARD_24K:
		case KEYBOARD_24K_DOUBLE:
			return Math.max(300.0, 7.605 * (totalnotes + 100) / (0.01 * totalnotes + 6.5));
		default:
			return Math.max(260.0, 7.605 * totalnotes / (0.01 * totalnotes + 6.5));
		}
	}
}

