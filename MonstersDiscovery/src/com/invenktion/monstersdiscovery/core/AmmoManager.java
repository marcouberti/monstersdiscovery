package com.invenktion.monstersdiscovery.core;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

import com.invenktion.monstersdiscovery.bean.AmmoBean;
import com.invenktion.monstersdiscovery.R;

public class AmmoManager {

	public static final String SPACEGUN = 		"SPACEGUN";
	public static final String SMARJE = 		"SMARJE";
	public static final String POWERSHOT = 		"POWERSHOT";
	public static final String BOMB = 			"BOMB";
	public static final String LASERCUT = 		"LASERCUT";
	public static final String SCISSOR = 		"SCISSOR";
	public static final String ROCKATTACK = 	"ROCKATTACK";
	public static final String PLASMANETOR = 	"PLASMANETOR";
	public static final String NUCLEAR = 		"NUCLEAR";
	public static final String DIABLITO = 		"DIABLITO";
	public static final String SHURIKEN = 		"SHURIKEN";
	public static final String BJOR = 			"BJOR";
	public static final String BONUSTIME = 		"BONUSTIME";
	public static final String MIX = 			"MIX";
	public static final String GNAUZ = 			"GNAUZ";
	public static final String SPINUZ = 		"SPINUZ";
	
	private static ArrayList<AmmoBean> ammos = new ArrayList<AmmoBean>();
	private static ArrayList<AmmoBean> unlockedAmmos = new ArrayList<AmmoBean>();
	
	public static ArrayList<AmmoBean> getAllAmmo() {
		return ammos;
	}
	public static void initializeAmmo() {
		ammos.clear();
		
		//ANTI ARMI
		AmmoBean antiammo1 = new AmmoBean(SMARJE,0,R.drawable.smarje,R.drawable.lucchetto,R.drawable.antiammo1instruction,false,null,-1);
		AmmoBean antiammo2 = new AmmoBean(BOMB,0,R.drawable.bomb,R.drawable.lucchetto,R.drawable.antiammo2instruction,false,null,-1);
		AmmoBean antiammo3 = new AmmoBean(SCISSOR,0,R.drawable.scissor,R.drawable.lucchetto,R.drawable.antiammo3instruction,false,null,-1);
		AmmoBean antiammo4 = new AmmoBean(GNAUZ,0,R.drawable.zeze,R.drawable.lucchetto,R.drawable.antiammo4instruction,false,null,-1);
		AmmoBean antiammo5 = new AmmoBean(SPINUZ,0,R.drawable.spinuz,R.drawable.lucchetto,R.drawable.antiammo5instruction,false,null,-1);
		AmmoBean antiammo6 = new AmmoBean(DIABLITO,0,R.drawable.diablo,R.drawable.lucchetto,R.drawable.antiammo6instruction,false,null,-1);
		AmmoBean antiammo7 = new AmmoBean(BJOR,0,R.drawable.bjor,R.drawable.lucchetto,R.drawable.antiammo7instruction,false,null,-1);
		AmmoBean antiammo8 = new AmmoBean(MIX,0,R.drawable.mix,R.drawable.lucchetto,R.drawable.antiammo8instruction,false,null,-1);
		
		AmmoBean ammo1 = new AmmoBean(SPACEGUN,ApplicationManager.AMMO_MIN_PRICE_VALUE,R.drawable.spacegun,R.drawable.lucchetto,R.drawable.ammo1instruction,true,antiammo1,R.drawable.arma1);
		ammos.add(ammo1);
		AmmoBean ammo2 = new AmmoBean(POWERSHOT,1000,R.drawable.powershot,R.drawable.lucchetto,R.drawable.ammo2instruction,true,antiammo2,R.drawable.arma2);
		ammos.add(ammo2);
		AmmoBean ammo3 = new AmmoBean(LASERCUT,4000,R.drawable.cutter,R.drawable.lucchetto,R.drawable.ammo3instruction,true,antiammo3,R.drawable.arma3);
		ammos.add(ammo3);
		AmmoBean ammo4 = new AmmoBean(ROCKATTACK,8000,R.drawable.missile,R.drawable.lucchetto,R.drawable.ammo4instruction,true,antiammo4,R.drawable.arma4);
		ammos.add(ammo4);
		AmmoBean ammo5 = new AmmoBean(PLASMANETOR,10000,R.drawable.plasmanetor,R.drawable.lucchetto,R.drawable.ammo5instruction,true,antiammo5,R.drawable.arma5);
		ammos.add(ammo5);
		AmmoBean ammo6 = new AmmoBean(NUCLEAR,15000,R.drawable.nuclear,R.drawable.lucchetto,R.drawable.ammo6instruction,true,antiammo6,R.drawable.arma6);
		ammos.add(ammo6);
		AmmoBean ammo7 = new AmmoBean(SHURIKEN,20000,R.drawable.shurikens,R.drawable.lucchetto,R.drawable.ammo7instruction,true,antiammo7,R.drawable.arma7);
		ammos.add(ammo7);
		AmmoBean ammo8 = new AmmoBean(BONUSTIME,50000,R.drawable.timetrick,R.drawable.lucchetto,R.drawable.ammo8instruction,true,antiammo8,R.drawable.arma8);
		ammos.add(ammo8);
	}
	
	public static AmmoBean thereIsAnUnlockedAmmo(Context context,int currentSkill) {
		int cont = 0;
		for(AmmoBean ammo:ammos) {
			if(!(ammo.isUnlocked(context))) {//controllo quelle non sbloccate di già
				if(ammo.getEnableCreditTrigger() <= currentSkill) {
					ammo.unlock(context);

            		//Salvo l'ultima arma sbloccata
            		SharedPreferences settings = context.getSharedPreferences(ApplicationManager.PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("last_ammo_unlocked", cont);
                    //Commit the edits!
                    editor.commit();

					return ammo;
				}
			}
			cont++;
		}
		return null;
	}
	
	public static AmmoBean getRandomAmmo() {
		int random = (int)(Math.random()*(unlockedAmmos.size()));
		if(random >= unlockedAmmos.size()) {
			random = 0;
		}
		//TEMP
		return unlockedAmmos.get(random);
		// DEBUG
		/*
		random = (int)(Math.random()*2);
		if(random ==1) {
			return ammos.get(4);
		}else {
			return ammos.get(4);
		}
		*/
	}
	
	public static void initializeUnlockedAmmo(Context context) {
		unlockedAmmos.clear();
		if(!ApplicationManager.DEBUG_MODE) {
			for(AmmoBean ammo: ammos) {
				if(ammo.isUnlocked(context)){
					int numberOfProbability = Integer.parseInt(ammo.getNumberOfProbability(context));
					//Aggiungo sia l'arma che l'antiarma
					for(int i=0; i<numberOfProbability; i++) {
						unlockedAmmos.add(ammo);
						unlockedAmmos.add(ammo.getAntiWeapon());
					}
				}
			}
		}else {
			unlockedAmmos.add(ammos.get(1));
			unlockedAmmos.add(ammos.get(1).getAntiWeapon());
		}
	}
}
