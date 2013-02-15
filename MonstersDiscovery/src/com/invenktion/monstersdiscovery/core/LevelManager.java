package com.invenktion.monstersdiscovery.core;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;

import com.invenktion.monstersdiscovery.bean.AmmoBean;
import com.invenktion.monstersdiscovery.bean.PictureBean;
import com.invenktion.monstersdiscovery.bean.SectionArrayList;
import com.invenktion.monstersdiscovery.R;


public class LevelManager {
	private static int currentLevel = 0;
	//Lista di tutte le sezioni
	private static ArrayList<SectionArrayList> sections = new ArrayList<SectionArrayList>();
	//La sezione del gioco corrente, cambierà a runtime con una reference ad una delle liste sotto.
	private static SectionArrayList<PictureBean> currentSection;
	private static SectionArrayList<PictureBean> firstSection = new SectionArrayList<PictureBean>();
	private static SectionArrayList<PictureBean> secondSection = new SectionArrayList<PictureBean>();
	private static SectionArrayList<PictureBean> thirdSection = new SectionArrayList<PictureBean>();
	private static SectionArrayList<PictureBean> fourthSection = new SectionArrayList<PictureBean>();
	private static SectionArrayList<PictureBean> fifthSection = new SectionArrayList<PictureBean>();
	
	//Sezione BONUS: è al di fuori delle modalità di gioco! Ogni quadro è indipendente.
	private static SectionArrayList<PictureBean> bonusSection = new SectionArrayList<PictureBean>();
	//Sezione SOLO in ATELIER: è al di fuori delle modalità di gioco! Si vedono sempre e solo nell'atelier, come quadri diprova.
	private static SectionArrayList<PictureBean> plusAtelierSection = new SectionArrayList<PictureBean>();

	public static void initializeLevels(Context context) {
		currentLevel = -1;
		
		sections.clear();
		sections.add(firstSection);
		sections.add(secondSection);
		sections.add(thirdSection);
		sections.add(fourthSection);
		sections.add(fifthSection);
		sections.add(bonusSection);
		sections.add(plusAtelierSection);
		
		//##########################################################
		//####################  QUADRI DI PROVA ATELIER ############
		//##########################################################
		plusAtelierSection.setSectionName("Atelier");
		plusAtelierSection.setStoryboardImage(R.drawable.sec1storyboard);
		plusAtelierSection.setSfondoImage(R.drawable.sfondo1);
		
		plusAtelierSection.setNumber(-1);
		//plusAtelierSection.setStoryboardImage(R.drawable.sec1storyboard);
		//plusAtelierSection.setPresentationImage(R.drawable.sec1presentation);
		//plusAtelierSection.setLockedImage(R.drawable.sec1locked);
		plusAtelierSection.setTelaImage(R.drawable.telaatelier);
		plusAtelierSection.setTelaFrontImage(R.drawable.fronteatelier);
		//plusAtelierSection.setCorniceImage(R.drawable.cornici1);
		plusAtelierSection.setSfondoImage(R.drawable.sfondo1);
		
		plusAtelierSection.clear();
		plusAtelierSection.add(new PictureBean("atm1",context.getString(R.string.atm1),R.drawable.aterlier1,R.drawable.aterlier1_tr,new int[] {
				Color.rgb(255,137,0),
				Color.rgb(176,62,72),
				Color.rgb(102,120,204)},
				31000,true,99));
		plusAtelierSection.add(new PictureBean("atm2",context.getString(R.string.atm2),R.drawable.aterlier2,R.drawable.aterlier2_tr,new int[] {
			Color.rgb(63,182,255),
			Color.rgb(255,191,50)},
			31000,true,99));
		
		//##########################################################
		//####################  1 SEZIONE ##########################
		//##########################################################
		firstSection.setSectionName("WORLD 1");
		firstSection.setNumber(0);
		firstSection.setStoryboardImage(R.drawable.sec1storyboard);
		firstSection.setPresentationImage(R.drawable.sec1presentation);
		firstSection.setLockedImage(R.drawable.sec1locked);
		firstSection.setTelaImage(R.drawable.tela1);
		firstSection.setTelaFrontImage(R.drawable.fronte1);
		//firstSection.setCorniceImage(R.drawable.cornici1);
		firstSection.setSfondoImage(R.drawable.sfondo1);
		firstSection.clear();
		
		firstSection.add(new PictureBean("w1m1",context.getString(R.string.w1m1),R.drawable.w1_monster1,R.drawable.w1_monster1_tr,new int[] {
			Color.rgb(199, 147, 216),
			Color.rgb(216, 17, 124),
			Color.rgb(155, 154, 216)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m2",context.getString(R.string.w1m2),R.drawable.w1_monster2,R.drawable.w1_monster2_tr,new int[] {
			Color.rgb(208, 221, 40),
			Color.rgb(219, 203, 121),
			Color.rgb(239, 239, 232),
			Color.rgb(204, 149, 51)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m3",context.getString(R.string.w1m3),R.drawable.w1_monster3,R.drawable.w1_monster3_tr,new int[] {
			Color.rgb(249, 135, 222),
			Color.rgb(173, 76, 150),
			Color.rgb(158, 150, 244)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m4",context.getString(R.string.w1m4),R.drawable.w1_monster4,R.drawable.w1_monster4_tr,new int[] {
			Color.rgb(127, 204, 190),
			Color.rgb(154, 63, 55),
			Color.rgb(185, 161, 143)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m5",context.getString(R.string.w1m5),R.drawable.w1_monster5,R.drawable.w1_monster5_tr,new int[] {
			Color.rgb(151, 198, 196),
			Color.rgb(206, 140, 167),
			Color.rgb(204, 209, 148)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m6",context.getString(R.string.w1m6),R.drawable.w1_monster6,R.drawable.w1_monster6_tr,new int[] {
			Color.rgb(255, 198, 72),
			Color.rgb(0, 143, 230),
			Color.rgb(229, 89, 11)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m7",context.getString(R.string.w1m7),R.drawable.w1_monster7,R.drawable.w1_monster7_tr,new int[] {
			Color.rgb(170, 193, 181),
			Color.rgb(146, 158, 191),
			Color.rgb(174, 143, 164),
			Color.rgb(117, 109, 79)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m8",context.getString(R.string.w1m8),R.drawable.w1_monster8,R.drawable.w1_monster8_tr,new int[] {
			Color.rgb(144, 108, 143),
			Color.rgb(226, 128, 10),
			Color.rgb(226, 64, 10),
			Color.rgb(56, 117, 231)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m9",context.getString(R.string.w1m9),R.drawable.w1_monster9,R.drawable.w1_monster9_tr,new int[] {
			Color.rgb(255, 196, 71),
			Color.rgb(254, 124, 46),
			Color.rgb(129, 109, 171)},
			31000,true,99));
		firstSection.add(new PictureBean("w1m10",context.getString(R.string.w1m10),R.drawable.w1_monster10,R.drawable.w1_monster10_tr,new int[] {
			Color.rgb(152, 132, 206),
			Color.rgb(77, 67, 129),
			Color.rgb(194, 108, 171)},
			31000,true,99));
		
		
		
		//##########################################################
		//####################  2 SEZIONE ##########################
		//##########################################################
		secondSection.setSectionName("WORLD 2");
		secondSection.setNumber(1);
		secondSection.setStoryboardImage(R.drawable.sec2storyboard);
		secondSection.setPresentationImage(R.drawable.sec2presentation);
		secondSection.setLockedImage(R.drawable.sec2locked);
		secondSection.setTelaImage(R.drawable.tela2);
		secondSection.setTelaFrontImage(R.drawable.fronte2);
		//secondSection.setCorniceImage(R.drawable.cornici2);
		secondSection.setSfondoImage(R.drawable.sfondo2);
		secondSection.clear();
		
		secondSection.add(new PictureBean("w2m1",context.getString(R.string.w2m1),R.drawable.w2_monster1,R.drawable.w2_monster1_tr,new int[] {
				Color.rgb(86, 184, 55),
				Color.rgb(43, 130, 153),
				Color.rgb(249, 58, 6),
				Color.rgb(250, 142, 56)},
				31000,true,99));
		secondSection.add(new PictureBean("w2m2",context.getString(R.string.w2m2),R.drawable.w2_monster2,R.drawable.w2_monster2_tr,new int[] {
				Color.rgb(205, 197, 0),
				Color.rgb(204, 124, 0),
				Color.rgb(204, 51, 0)},
				31000,false,99));
		secondSection.add(new PictureBean("w2m3",context.getString(R.string.w2m3),R.drawable.w2_monster3,R.drawable.w2_monster3_tr,new int[] {
				Color.rgb(161, 178, 98),
				Color.rgb(178, 118, 98),
				Color.rgb(111, 124, 162)},
				31000,false,99));
		secondSection.add(new PictureBean("w2m4",context.getString(R.string.w2m4),R.drawable.w2_monster4,R.drawable.w2_monster4_tr,new int[] {
				Color.rgb(107, 129, 148),
				Color.rgb(217, 224, 33),
				Color.rgb(221, 153, 33),
				Color.rgb(145, 95, 7)},
				31000,false,99));
		
		secondSection.add(new PictureBean("w2m5",context.getString(R.string.w2m5),R.drawable.w2_monster5,R.drawable.w2_monster5_tr,new int[] {
				Color.rgb(150, 86, 120),
				Color.rgb(150, 176, 215),
				Color.rgb(221, 179, 124),
				Color.rgb(247, 229, 169)},
				31000,false,99));
		secondSection.add(new PictureBean("w2m6",context.getString(R.string.w2m6),R.drawable.w2_monster6,R.drawable.w2_monster6_tr,new int[] {
				Color.rgb(213, 216, 122),
				Color.rgb(226, 122, 237),
				Color.rgb(114, 142, 72)},
				31000,false,99));
	
		secondSection.add(new PictureBean("w2m7",context.getString(R.string.w2m7),R.drawable.w2_monster7,R.drawable.w2_monster7_tr,new int[] {
				Color.rgb(154, 116, 188),
				Color.rgb(150, 196, 234),
				Color.rgb(188, 116, 154)},
				31000,false,99));
		secondSection.add(new PictureBean("w2m8",context.getString(R.string.w2m8),R.drawable.w2_monster8,R.drawable.w2_monster8_tr,new int[] {
				Color.rgb(198, 193, 23),
				Color.rgb(167, 51, 63),
				Color.rgb(98, 99, 26)},
				31000,false,99));
		secondSection.add(new PictureBean("w2m9",context.getString(R.string.w2m9),R.drawable.w2_monster9,R.drawable.w2_monster9_tr,new int[] {
			Color.rgb(154,191,17),
			Color.rgb(124,133,163),
			Color.rgb(153,112,105)},
			31000,false,99));
		secondSection.add(new PictureBean("w2m10",context.getString(R.string.w2m10),R.drawable.w2_monster10,R.drawable.w2_monster10_tr,new int[] {
			Color.rgb(201,175,0),
			Color.rgb(87,150,180),
			Color.rgb(180,108,87)},
			31000,false,99));
		
		
		
		//##########################################################
		//####################  3 SEZIONE ##########################
		//##########################################################
		thirdSection.setSectionName("WORLD 3");
		thirdSection.setNumber(2);
		thirdSection.setStoryboardImage(R.drawable.sec3storyboard);
		thirdSection.setPresentationImage(R.drawable.sec3presentation);
		thirdSection.setLockedImage(R.drawable.sec3locked);
		thirdSection.setTelaImage(R.drawable.tela3);
		thirdSection.setTelaFrontImage(R.drawable.fronte3);
		//thirdSection.setCorniceImage(R.drawable.cornici3);
		thirdSection.setSfondoImage(R.drawable.sfondo3);
		thirdSection.clear();
		
		thirdSection.add(new PictureBean("w3m1",context.getString(R.string.w3m1),R.drawable.w3_monster1,R.drawable.w3_monster1_tr,new int[] {
				Color.rgb(239, 221, 175),
				Color.rgb(186, 143, 185),
				Color.rgb(239, 191, 175),
				Color.rgb(103, 163, 117)},
				31000,true,99));
		thirdSection.add(new PictureBean("w3m2",context.getString(R.string.w3m2),R.drawable.w3_monster2,R.drawable.w3_monster2_tr,new int[] {
				Color.rgb(149, 187, 206),
				Color.rgb(192, 166, 160),
				Color.rgb(214, 155, 112),
				Color.rgb(93, 103, 110)},
				31000,false,99));
		thirdSection.add(new PictureBean("w3m3",context.getString(R.string.w3m3),R.drawable.w3_monster3,R.drawable.w3_monster3_tr,new int[] {
				Color.rgb(237, 54, 32),
				Color.rgb(199, 148, 85),
				Color.rgb(76, 131, 214)},
				31000,false,99));
		thirdSection.add(new PictureBean("w3m4",context.getString(R.string.w3m4),R.drawable.w3_monster4,R.drawable.w3_monster4_tr,new int[] {
				Color.rgb(199, 109, 201),
				Color.rgb(196, 200, 156),
				Color.rgb(216, 111, 111),
				Color.rgb(210, 169, 156)},
				31000,false,99));
		thirdSection.add(new PictureBean("w3m5",context.getString(R.string.w3m5),R.drawable.w3_monster5,R.drawable.w3_monster5_tr,new int[] {
				Color.rgb(189, 204, 212),
				Color.rgb(219, 168, 152),
				Color.rgb(121, 101, 132),
				Color.rgb(143, 119, 80)},
				31000,false,99));
		thirdSection.add(new PictureBean("w3m6",context.getString(R.string.w3m6),R.drawable.w3_monster6,R.drawable.w3_monster6_tr,new int[] {
				Color.rgb(244, 110, 0),
				Color.rgb(121, 63, 63),
				Color.rgb(98, 69, 146)},
				31000,false,99));
		thirdSection.add(new PictureBean("w3m7",context.getString(R.string.w3m7),R.drawable.w3_monster7,R.drawable.w3_monster7_tr,new int[] {
				Color.rgb(202, 130, 95),
				Color.rgb(150, 52, 5),
				Color.rgb(14, 206, 226),
				Color.rgb(226, 85, 14)},
				31000,false,99));
		thirdSection.add(new PictureBean("w3m8",context.getString(R.string.w3m8),R.drawable.w3_monster8,R.drawable.w3_monster8_tr,new int[] {
				Color.rgb(229, 166, 244),
				Color.rgb(18, 185, 136),
				Color.rgb(218, 88, 18),
				Color.rgb(18, 118, 177),
				Color.rgb(219, 148, 18)},
				31000,false,99));
		thirdSection.add(new PictureBean("w3m9",context.getString(R.string.w3m9),R.drawable.w3_monster9,R.drawable.w3_monster9_tr,new int[] {
				Color.rgb(41, 171, 226),
				Color.rgb(134, 129, 134),
				Color.rgb(231, 135, 88)},
				31000,false,99));
		thirdSection.add(new PictureBean("w3m10",context.getString(R.string.w3m10),R.drawable.w3_monster10,R.drawable.w3_monster10_tr,new int[] {
				Color.rgb(171, 168, 171),
				Color.rgb(87, 150, 180),
				Color.rgb(157, 120, 110)},
				31000,false,99));
		
		//##########################################################
		//####################  4 SEZIONE ##########################
		//##########################################################
		fourthSection.setSectionName("WORLD 4");
		fourthSection.setNumber(3);
		fourthSection.setStoryboardImage(R.drawable.sec4storyboard);
		fourthSection.setPresentationImage(R.drawable.sec4presentation);
		fourthSection.setLockedImage(R.drawable.sec4locked);
		fourthSection.setTelaImage(R.drawable.tela4);
		fourthSection.setTelaFrontImage(R.drawable.fronte4);
		//fourthSection.setCorniceImage(R.drawable.cornici4);
		fourthSection.setSfondoImage(R.drawable.sfondo4);
		fourthSection.clear();
		
		fourthSection.add(new PictureBean("w4m1",context.getString(R.string.w4m1),R.drawable.w4_monster1,R.drawable.w4_monster1_tr,new int[] {
				Color.rgb(177, 198, 229),
				Color.rgb(201, 148, 174),
				Color.rgb(255, 204, 77)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m2",context.getString(R.string.w4m2),R.drawable.w4_monster2,R.drawable.w4_monster2_tr,new int[] {
				Color.rgb(226, 199, 10),
				Color.rgb(73, 79, 98),
				Color.rgb(231, 83, 33)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m3",context.getString(R.string.w4m3),R.drawable.w4_monster3,R.drawable.w4_monster3_tr,new int[] {
				Color.rgb(204, 62, 23),
				Color.rgb(126, 40, 12),
				Color.rgb(135, 213, 45)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m4",context.getString(R.string.w4m4),R.drawable.w4_monster4,R.drawable.w4_monster4_tr,new int[] {
				Color.rgb(191, 209, 134),
				Color.rgb(54, 92, 26),
				Color.rgb(121, 86, 134),
				Color.rgb(236, 80, 107),
				Color.rgb(158, 86, 62)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m5",context.getString(R.string.w4m5),R.drawable.w4_monster5,R.drawable.w4_monster5_tr,new int[] {
				Color.rgb(255, 95, 6),
				Color.rgb(70, 224, 224)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m6",context.getString(R.string.w4m6),R.drawable.w4_monster6,R.drawable.w4_monster6_tr,new int[] {
				Color.rgb(250, 161, 255),
				Color.rgb(148, 204, 255),
				Color.rgb(112, 115, 161),
				Color.rgb(207, 255, 135)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m7",context.getString(R.string.w4m7),R.drawable.w4_monster7,R.drawable.w4_monster7_tr,new int[] {
				Color.rgb(165, 175, 181),
				Color.rgb(155, 67, 48),
				Color.rgb(64, 121, 155)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m8",context.getString(R.string.w4m8),R.drawable.w4_monster8,R.drawable.w4_monster8_tr,new int[] {
				Color.rgb(244, 198, 3),
				Color.rgb(56, 56, 56),
				Color.rgb(244, 97, 3),
				Color.rgb(125, 149, 168)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m9",context.getString(R.string.w4m9),R.drawable.w4_monster9,R.drawable.w4_monster9_tr,new int[] {
				Color.rgb(78, 198, 34),
				Color.rgb(77, 142, 175),
				Color.rgb(198, 75, 34)},
				31000,false,99));
		fourthSection.add(new PictureBean("w4m10",context.getString(R.string.w4m10),R.drawable.w4_monster10,R.drawable.w4_monster10_tr,new int[] {
				Color.rgb(95, 198, 211),
				Color.rgb(211, 194, 116),
				Color.rgb(160, 69, 87)},
				31000,false,99));
				
		
		
		//##########################################################
		//####################  5 SEZIONE ##########################
		//##########################################################
		fifthSection.setSectionName("WORLD 5");
		fifthSection.setNumber(4);
		fifthSection.setStoryboardImage(R.drawable.sec5storyboard);
		fifthSection.setPresentationImage(R.drawable.sec5presentation);
		fifthSection.setLockedImage(R.drawable.sec5locked);
		fifthSection.setTelaImage(R.drawable.tela5);
		fifthSection.setTelaFrontImage(R.drawable.fronte5);
		//fifthSection.setCorniceImage(R.drawable.cornici5);
		fifthSection.setSfondoImage(R.drawable.sfondo5);
		fifthSection.clear();
		
		fifthSection.add(new PictureBean("w5m1",context.getString(R.string.w5m1),R.drawable.w5_monster1,R.drawable.w5_monster1_tr,new int[] {
				Color.rgb(79, 133, 213),
				Color.rgb(132, 216, 75),
				Color.rgb(247, 25, 115)},
				31000,false,99));
		fifthSection.add(new PictureBean("w5m2",context.getString(R.string.w5m2),R.drawable.w5_monster2,R.drawable.w5_monster2_tr,new int[] {
				Color.rgb(117, 193, 58),
				Color.rgb(205, 177, 61)},
				31000,true,99));
		fifthSection.add(new PictureBean("w5m3",context.getString(R.string.w5m3),R.drawable.w5_monster3,R.drawable.w5_monster3_tr,new int[] {
				Color.rgb(216, 97, 219),
				Color.rgb(251, 63, 201),
				Color.rgb(176, 139, 251)},
				31000,false,99));
		fifthSection.add(new PictureBean("w5m4",context.getString(R.string.w5m4),R.drawable.w5_monster4,R.drawable.w5_monster4_tr,new int[] {
				Color.rgb(82, 199, 239),
				Color.rgb(239, 121, 82),
				Color.rgb(213, 151, 0)},
				31000,false,99));
		fifthSection.add(new PictureBean("w5m5",context.getString(R.string.w5m5),R.drawable.w5_monster5,R.drawable.w5_monster5_tr,new int[] {
				Color.rgb(242, 186, 28),
				Color.rgb(165, 83, 3),
				Color.rgb(165, 97, 160)},
				31000,false,99));
		fifthSection.add(new PictureBean("w5m6",context.getString(R.string.w5m6),R.drawable.w5_monster6,R.drawable.w5_monster6_tr,new int[] {
				Color.rgb(206, 118, 74),
				Color.rgb(129, 62, 34),
				Color.rgb(101, 130, 34)},
				31000,false,99));
		fifthSection.add(new PictureBean("w5m7",context.getString(R.string.w5m7),R.drawable.w5_monster7,R.drawable.w5_monster7_tr,new int[] {
				Color.rgb(188, 94, 164),
				Color.rgb(111, 67, 100),
				Color.rgb(134, 196, 193),
				Color.rgb(206, 173, 162)},
				31000,false,99));
		fifthSection.add(new PictureBean("w5m8",context.getString(R.string.w5m8),R.drawable.w5_monster8,R.drawable.w5_monster8_tr,new int[] {
				Color.rgb(209, 137, 21),
				Color.rgb(217, 190, 65),
				Color.rgb(134, 103, 96),
				Color.rgb(209, 68, 21)},
				31000,false,99));
		fifthSection.add(new PictureBean("w5m9",context.getString(R.string.w5m9),R.drawable.w5_monster9,R.drawable.w5_monster9_tr,new int[] {
				Color.rgb(232, 86, 86),
				Color.rgb(112, 170, 124),
				Color.rgb(54, 88, 112)},
				31000,false,99));
		fifthSection.add(new PictureBean("w5m10",context.getString(R.string.w5m10),R.drawable.w5_monster10,R.drawable.w5_monster10_tr,new int[] {
				Color.rgb(242, 104, 39),
				Color.rgb(79, 86, 229),
				Color.rgb(127, 32, 0),
				Color.rgb(242, 221, 156)},
				31000,false,99));
				
		
		//##########################################################
		//##############  6 SEZIONE BONUS ##########################
		//##########################################################
		bonusSection.setSectionName("Bonus");
		bonusSection.setNumber(5);
		bonusSection.setStoryboardImage(R.drawable.sec1storyboard);
		bonusSection.setPresentationImage(R.drawable.bonuspresentation);
		bonusSection.setLockedImage(R.drawable.bonuspresentation);
		bonusSection.setTelaImage(R.drawable.gb_retro);
		bonusSection.setTelaFrontImage(R.drawable.gb_fronte);
		//bonusSection.setCorniceImage(R.drawable.cornici1);
		bonusSection.setSfondoImage(R.drawable.sfondobonus);
		bonusSection.clear();
		
		bonusSection.add(new PictureBean("wbm1",context.getString(R.string.wbm1),R.drawable.wb_monster1,R.drawable.wb_monster1_tr,new int[] {
				Color.rgb(53, 53, 68),
				Color.rgb(193, 0, 0),
				Color.rgb(255, 255, 255),
				Color.rgb(132, 103, 73)},
				31000,true,99));
		bonusSection.add(new PictureBean("wbm2",context.getString(R.string.wbm2),R.drawable.wb_monster2,R.drawable.wb_monster2_tr,new int[] {
				Color.rgb(77, 117, 140),
				Color.rgb(243,251,255),
				Color.rgb(252,167,196)},
				31000,false,99));
		bonusSection.add(new PictureBean("wbm3",context.getString(R.string.wbm3),R.drawable.wb_monster3,R.drawable.wb_monster3_tr,new int[] {
				Color.rgb(147,71,45),
				Color.rgb(249,192,192),
				Color.rgb(223,154,38)},
				31000,false,99));
		bonusSection.add(new PictureBean("wbm4",context.getString(R.string.wbm4),R.drawable.wb_monster4,R.drawable.wb_monster4_tr,new int[] {
				Color.rgb(88, 149, 204),
				Color.rgb(99,158,151),
				Color.rgb(114,128,168),
				Color.rgb(77,94,87)},
				31000,false,99));
		bonusSection.add(new PictureBean("wbm5",context.getString(R.string.wbm5),R.drawable.wb_monster5,R.drawable.wb_monster5_tr,new int[] {
				Color.rgb(54, 59,88),
				Color.rgb(216,216,216),
				Color.rgb(255,145,0),
				Color.rgb(164,96,73)},
				31000,false,99));
		
		//La prima sezione è sbloccata di default
		//Il primo livello della prima sezione è SBLOCCATO di default
		((PictureBean)(sections.get(0).get(0))).unlockLevel(context, "arcade");
		sections.get(0).unlockSection(context, "arcade");
		
		//I quadri trial dell'atelier sono sblocati di default
		for(PictureBean bean: plusAtelierSection) {
			bean.unlockLevel(context, "arcade");
		}
		
		//##########################
		//Precarico tutte le bitmap 
		//con softreference, cosi i 
		//menù risultano più fluidi
		//##########################
		int cont = 0;
		for(SectionArrayList<PictureBean> sec:sections) {
			if(cont >= (sections.size() -1)) break;//la sezione trial atelier non la considero
			sec.getPresentaionImage(context);
			sec.getLockedImage(context);
			//precached all levels colored image
			
			//for(PictureBean pic:sec){
				//pic.getColoredPicture(context);
			//}
			cont++;
		}
		//All'inizio
		currentSection = firstSection;
	}

	public static void clearAllCachedImage() {
		for(SectionArrayList<PictureBean> sec:sections) {
			sec.clearSoftReferences();
			for(PictureBean pic:sec){
				pic.clearSoftReferences();
			}
		}
		for(AmmoBean ammo: AmmoManager.getAllAmmo()) {
			ammo.clearSoftReferences();
		}
	}
	
	public static ArrayList<PictureBean> getAllLevels() {
		return currentSection;
	}
	
	public static int getCurrentLevelIndex() {
		return currentLevel;
	}
	
	public static void setCurrentLevelIndex(int currentLevel) {
		if(currentLevel < currentSection.size() && currentLevel >= 0) {
			LevelManager.currentLevel = currentLevel;
		}
	}
	
	public static PictureBean getCurrentLevel() {
		return currentSection.get(currentLevel);
	}
	
	public static PictureBean getNextLevel() {
		if(currentLevel < currentSection.size()-1) {
			return currentSection.get(currentLevel+1);
		}else return null;
	}
	
	public static PictureBean getPreviousLevel() {
		if(currentLevel > 0) {
			currentLevel--;
			return currentSection.get(currentLevel);
		}else return null;
	}
	
	public static Integer[] getAllLevelColorResourceId() {
		int count = currentSection.size();
		Integer[] resourcesId = new Integer[count];
		
		for(int i=0; i<count; i++) {
			resourcesId[i] = currentSection.get(i).getColoredPicture();
		}
		return resourcesId;
	}

	public static int getLevelCount() {
		return currentSection.size();
	}
	
	public static ArrayList<SectionArrayList> getAllSections() {
		return sections;
	}

	public static int getCurrentSectionIndex() {
		return currentSection.getNumber();
	}

	public static int getSectionCount() {
		return sections.size();
	}

	public static SectionArrayList<PictureBean> getBonusSection() {
		return bonusSection;
	}
	
	public static SectionArrayList<PictureBean> getAtelierTrialSection() {
		return plusAtelierSection;
	}
	
	public static void setCurrentSection(int position) {
		/*
		if(position == 0) {
			currentSection = firstSection;
		}else if(position == 1){
			currentSection = secondSection;
		}*/
		if(position >= 0 && position < sections.size()) {
			currentSection = sections.get(position);
		}
		currentLevel = -1;
	}
	
	public static SectionArrayList<PictureBean> getCurrentSection() {
		return currentSection;
	}
	
	//Ritorna la prossima sezione se esiste, altrimenti null
	public static SectionArrayList<PictureBean> getNextSection() {
		int sezioneAttuale = getCurrentSectionIndex();
		if(sezioneAttuale < sections.size()-1) {
			return sections.get(sezioneAttuale + 1);
		}else return null;//non ci sono altre sezioni dopo questa
	}

}
