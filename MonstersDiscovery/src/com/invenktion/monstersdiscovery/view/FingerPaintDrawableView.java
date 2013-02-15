package com.invenktion.monstersdiscovery.view;

import com.invenktion.monstersdiscovery.DrawChallengeActivity;
import com.invenktion.monstersdiscovery.bean.AmmoBean;
import com.invenktion.monstersdiscovery.bean.PictureBean;
import com.invenktion.monstersdiscovery.core.AmmoManager;
import com.invenktion.monstersdiscovery.core.AnimationFactory;
import com.invenktion.monstersdiscovery.core.ApplicationManager;
import com.invenktion.monstersdiscovery.core.TimeManager;
import com.invenktion.monstersdiscovery.R;
import com.invenktion.monstersdiscovery.utils.ColorUtils;
import com.invenktion.monstersdiscovery.utils.FilterUtils;
import com.samsung.spen.settings.SettingStrokeInfo;

import android.content.Context;
import android.graphics.*;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;


public class FingerPaintDrawableView extends View implements OnTouchListener{

	public static int FULL_ALPHA = 255;
	public static int TRANSPARENCY_ALPHA = 70;
	
	private Context context;
	
	private Paint       mPaint;
	private Paint       fillPaint;//per i cerchi pieni all'inizio del tracciato, per fare i puntini
	private Paint       linePaint;//per la croce di supporto al disegno
    private MaskFilter  mEmboss;
    private MaskFilter  mBlur;
    
    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;
	private static final int ELABORATION_SIZE = 200;
	//public static final int MAX_BITMAP_SIZE = 400;
    
    //quelle che utilizzo per i calcoli, senza filtraggi, belle grezze
    private Bitmap contour;
    private Bitmap colored;
    //quelle che disegno, belle alla vista smussate
    private Bitmap contourFiltered;
    private Bitmap coloredFiltered;
    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    private Paint   mContourPaint;
    private Paint   mResultPaint;
    
    private int mBitmpaPaintAlpha = 255;
    
    private int VIEW_HEIGHT,VIEW_WIDTH = 0;
	private int CX,CY =0;
	
	float DENSITY = 1.0f;
	
	private boolean showResult = true;
	private boolean resultComputed = false;
	private boolean elaborationRunning = false;//per evitare multiple elaborazioni contemporaneamente
	private PictureBean picture;
	
	DrawChallengeActivity dashboardActivity;

	//private int fingerX,fingerY;
	private boolean isFingerDown = false;
	
	//private float touchNormalization;
	private Rect destRect;
	
    public FingerPaintDrawableView(Context context, DrawChallengeActivity dashboardActivity) {
		super(context);
		this.context = context;
		this.dashboardActivity = dashboardActivity;
		this.DENSITY = context.getResources().getDisplayMetrics().density;
        mPath = new Path();
        //mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(false);
        mBitmapPaint.setFilterBitmap(false);
        
        mContourPaint = new Paint();
        mContourPaint.setAntiAlias(false);
        mContourPaint.setFilterBitmap(true);
        
        mResultPaint = new Paint();
        mPaint = new Paint();
        //mPaint.setAntiAlias(true);
        //mPaint.setDither(true);
        //mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(ApplicationManager.getPaintSize());
        //Questo mi serve per la gomma ad esempio, che se pitturi la trasparenza, cancella quello che c'� sotto.
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        
        linePaint= new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        linePaint.setStrokeWidth(2);
        linePaint.setPathEffect( new DashPathEffect(new float[] { 15, 5, 8, 5 },0) );//TRATTEGGIO

        //mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },0.4f, 6, 3.5f);
        //mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        //mPaint.setMaskFilter(mEmboss);
        //mPaint.setMaskFilter(mBlur);
        
        
        //######### MUB S CANVAS NON RICHIEDE QUESTO LISTENER
        //setOnTouchListener(this);
	}

    public void recycleBitmaps() {
    	
    	////Log.d("RECICLO FINGER BITMPA","##################");
		if(contour != null && !contour.isRecycled()) {
			contour.recycle();
			contour = null;
		}
		if(colored != null && !colored.isRecycled()) {
			colored.recycle();
			colored = null;
		}
		if(coloredFiltered != null && !coloredFiltered.isRecycled()) {
			coloredFiltered.recycle();
			coloredFiltered = null;
		}
		if(contourFiltered != null && !contourFiltered.isRecycled()) {
			contourFiltered.recycle();
			contourFiltered = null;
		}
		if(mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
		if(resultBitmap != null && !resultBitmap.isRecycled()) {
			resultBitmap.recycle();
			resultBitmap = null;
		}
		if(appendBitmap != null && !appendBitmap.isRecycled()) {
			appendBitmap.recycle();
			appendBitmap = null;
		}
		if(backgroundBtm != null && !backgroundBtm.isRecycled()) {
			backgroundBtm.recycle();
			backgroundBtm = null;
		}
		System.gc();
		
	}
    
    public PictureBean getPicture() {
		return picture;
	}



	public int getmBitmpaPaintAlpha() {
		return mBitmpaPaintAlpha;
	}

	public void setmBitmpaPaintAlpha(int mBitmpaPaintAlpha) {
		this.mBitmpaPaintAlpha = mBitmpaPaintAlpha;
		this.invalidate();
	}

	public void setShowResult(boolean showResult) {
		this.showResult = showResult;
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	VIEW_WIDTH = getWidth();
    	VIEW_HEIGHT = getHeight();
    	CY = VIEW_HEIGHT/2;
    	CX = VIEW_WIDTH/2;
    	
    	//touchNormalization = (float)SCREEN_HEIGHT/(float)MAX_BITMAP_SIZE;
    	destRect = new Rect(CX-(VIEW_WIDTH/2),0,CX+(VIEW_WIDTH/2),VIEW_HEIGHT);
    
    	initializeBitmaps();
    }
	
	public void setPicture(PictureBean picture) {
		this.picture = picture;
		this.mPaint.setColor(picture.getColors()[0]);
		this.fillPaint.setColor(picture.getColors()[0]);
		ApplicationManager.setCurrentColor(picture.getColors()[0]);
		changeSPENColor(picture.getColors()[0]);
		this.resultComputed = false;
	}
    
    public void initializeBitmaps() {
    	//recycleBitmaps();
    	//Preparo le immagini grandi al minimo indispensabile per le performance
    	
    	Options opts = new BitmapFactory.Options();
    	opts.inSampleSize=1;
    	Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    	opts.inPreferredConfig = conf;
    	
    	contourFiltered = BitmapFactory.decodeResource(getResources(), picture.getContourPicture());     
		contour = Bitmap.createScaledBitmap(contourFiltered, ELABORATION_SIZE, ELABORATION_SIZE, true);
		//contourFiltered = Bitmap.createScaledBitmap(bmp, SCREEN_HEIGHT, SCREEN_HEIGHT, true);
		//contourFiltered = Bitmap.createScaledBitmap(bmp, MAX_BITMAP_SIZE, MAX_BITMAP_SIZE, true);
		//bmp.recycle();
		//bmp = null;
	
		coloredFiltered = BitmapFactory.decodeResource(getResources(), picture.getColoredPicture());     
		colored = Bitmap.createScaledBitmap(coloredFiltered, ELABORATION_SIZE, ELABORATION_SIZE, false);
		//coloredFiltered = Bitmap.createScaledBitmap(bmpColore, SCREEN_HEIGHT, SCREEN_HEIGHT, true);
		//coloredFiltered = Bitmap.createScaledBitmap(bmpColore, MAX_BITMAP_SIZE, MAX_BITMAP_SIZE, true);
		//bmpColore.recycle();
		//bmpColore= null;
    	
		mBitmap = Bitmap.createBitmap(VIEW_HEIGHT, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
		//mBitmap = Bitmap.createBitmap(MAX_BITMAP_SIZE, MAX_BITMAP_SIZE, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		//All'inizio disegno sopra l'immagine a colori in trasparenza lieve, come aiuto a chi disegna
		//Ho dovuto rimuovere questa immagine in trasparenza dal livello sotto, altrimenti le
		//performance erano scarse, soprattutto su dispositivi grandi e/o scarsi.
		Paint traspPaint =new Paint();
		traspPaint.setAlpha(FingerPaintDrawableView.TRANSPARENCY_ALPHA);
		mCanvas.drawBitmap(coloredFiltered,null,destRect,traspPaint);
		traspPaint = null;
        if(mPath != null) {
        	mPath.reset();
        }
        
        //SAMSUNG S PEN SDK, imposto il background del canvas
        setCenteredBackgroundOnSCanvas(mBitmap,mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
        //NOSystem.gc();
    }
    
    Bitmap backgroundBtm;
    private void setCenteredBackgroundOnSCanvas(Bitmap bitmap,Paint paint,int W,int H) {
    	double SW = dashboardActivity.mCanvasView.getWidth();
        double SH = dashboardActivity.mCanvasView.getHeight();
        backgroundBtm = Bitmap.createBitmap((int)SW, (int)SH, Bitmap.Config.ARGB_8888);
        Canvas bgCanvas = new Canvas(backgroundBtm);
        Rect destRect = new Rect((int)(SW/2 - W/2),(int)(SH/2 - H/2),W+(int)(SW/2 - W/2),H+(int)(SH/2 - H/2));
        bgCanvas.drawBitmap(bitmap, null,destRect, paint);
        
        dashboardActivity.mCanvasView.setBackgroundImage(backgroundBtm);
        
        backgroundBtm.recycle();
        backgroundBtm = null;
        //bitmap.recycle();
        //bitmap = null;
    }
    
    Bitmap appendBitmap;
    private void appendCenteredBitmapOnSCanvas(Bitmap bitmap,Paint paint,int W,int H) {
    	double SW = dashboardActivity.mCanvasView.getWidth();
        double SH = dashboardActivity.mCanvasView.getHeight();
        appendBitmap = dashboardActivity.mCanvasView.getBitmap(true);
        Canvas bgCanvas = new Canvas(appendBitmap);
        Rect destRect = new Rect((int)(SW/2 - W/2),(int)(SH/2 - H/2),W+(int)(SW/2 - W/2),H+(int)(SH/2 - H/2));
        bgCanvas.drawBitmap(bitmap, null,destRect, paint);
        dashboardActivity.mCanvasView.setBitmap(appendBitmap,true);
        appendBitmap.recycle();
        appendBitmap = null;
        //NB si occupa il chiamante di recycle la bitmap
        //bitmap.recycle();
        //bitmap = null;
    }
    
    float percentage = 0f;
    float startingSuccessPercentage;
 	Bitmap resultBitmap = null;
    @Override
    protected void onDraw(Canvas canvas) {
    	if(destRect != null) {
			//Qui metter� un effetto dissolvenza del colore al 3,2,1 via, TODO
	    	//mBitmapPaint.setAlpha(mBitmpaPaintAlpha);
	    	//L'immagine colorata la facciamo vedere solo all'inizio, dopo no xch� � troppo
	    	//pesante e il pennello rallenta troppo
	    	if(coloredFiltered != null && this.mBitmpaPaintAlpha == 255) {
	    		//Rect destRect = new Rect(CX-(SCREEN_WIDTH/2),0,CX+(SCREEN_WIDTH/2),SCREEN_HEIGHT);
	    		canvas.drawBitmap(coloredFiltered, null, destRect, mBitmapPaint);
	        	//canvas.drawBitmap(coloredFiltered, CX-(SCREEN_WIDTH/2), CY-(SCREEN_WIDTH/2), mBitmapPaint); 
	        }
	    	//mBitmapPaint.setAlpha(255);
	    	/* SOSTITUITO DA SPEN SDK SAMSUNG
	    	if(mBitmap != null) {
	    		//Rect destRect = new Rect(CX-(SCREEN_WIDTH/2),0,CX+(SCREEN_WIDTH/2),SCREEN_HEIGHT);
	    		canvas.drawBitmap(mBitmap, null, destRect, mBitmapPaint);
	    		//canvas.drawBitmap(mBitmap, CX-(SCREEN_WIDTH/2), CY-(SCREEN_WIDTH/2), mBitmapPaint);
	    	}
	    	*/
	    	//Se non � la gomma pitturo direttamente sul canvas
	    	//if(!(mPaint.getColor() == ApplicationManager.TRANSPARENT_COLOR)) {
	    		//canvas.drawPath(mPath, mPaint);
	    	//}
	        //canvas.drawPath(mPath, mPaint);
	        if(contourFiltered != null) {
	        	//Rect destRect = new Rect(CX-(SCREEN_WIDTH/2),0,CX+(SCREEN_WIDTH/2),SCREEN_HEIGHT);
	    		canvas.drawBitmap(contourFiltered, null, destRect, mContourPaint);
	        	//canvas.drawBitmap(contourFiltered, CX-(SCREEN_WIDTH/2), CY-(SCREEN_WIDTH/2), mBitmapPaint); 
	        }
	        
	        //Se il dito del disegnatore � giu disegno la croce di supporto al disegno
	        /*
	    	if(isFingerDown){
	    		canvas.drawCircle(fingerX,fingerY, paintSize/2, linePaint);
	    		canvas.drawLine(fingerX, fingerY, fingerX-100, fingerY, linePaint);
	    		canvas.drawLine(fingerX, fingerY, fingerX+100, fingerY, linePaint);
	    		canvas.drawLine(fingerX, fingerY, fingerX, fingerY+100, linePaint);
	    		canvas.drawLine(fingerX, fingerY, fingerX, fingerY-100, linePaint);
	    	}
	    	*/
    	}
    }
    
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 2;
    int paintSize = 1;
    private void touch_start(float x, float y) {
    	paintSize = ApplicationManager.getPaintSize();
    	mPaint.setStrokeWidth(paintSize);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        //mCanvas.drawCircle(x, y, paintSize/2, fillPaint);
        //Posizione del dito
        //fingerX = (int)x;
        //fingerY = (int)y;
        isFingerDown = true;
        //mCanvas.drawOval(new RectF(x-(paintSize/4),y-(paintSize/4),x+(paintSize/4),y+(paintSize/4)), mPaint);
    }
    private void touch_move(float x, float y,boolean PINCH_ZOOM) {
        
    	if(PINCH_ZOOM) return;
    	
    	float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            //mPath.lineTo(x,y);
            mX = x;
            mY = y;
            //draw the path to our offscreen
            //Prima lo facevo solo sul touch up, ma devo disegnare per 
            //quando finisce il tempo e il dito � ancora giu
            //mCanvas.drawPath(mPath, mPaint);
            
            //Pitturo subito solo se trasparente
            //if(mPaint.getColor() == ApplicationManager.TRANSPARENT_COLOR) {
            	mCanvas.drawPath(mPath, mPaint);
            //}
        }
        //Posizione del dito
        //fingerX = (int)x;
        //fingerY = (int)y;
    }
    private void touch_up(boolean afterPinchZoom) {
    	if(!afterPinchZoom) {
    		mPath.lineTo(mX+1, mY+1);//il +1 serve a fare disegnare i punti (toccata e fuga)
    	}
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        //Posizione del dito alzato
        isFingerDown = false;
    }

    private boolean PINCH_ZOOM = false;
	public boolean onTouch(View v, MotionEvent event) {
    	 if(showResult) {
    	 	 return false;
    	 }
    	 ////Log.d("","event: "+event.getAction());
		 float x = event.getX();///touchNormalization;
	     float y = event.getY();///touchNormalization;
	     
	     switch (event.getAction()) {
	         case MotionEvent.ACTION_DOWN:
	             touch_start(x, y);
	             invalidate();
	             break;
	         case MotionEvent.ACTION_MOVE:
	        	 /*
	        	 if(event.getPointerCount() == 2) {//sto usando 2 dita
	        		 PINCH_ZOOM = true;
	        		 computePinchZoom(event);
	        	 }else {
	        	 */
	        		 touch_move(x, y,PINCH_ZOOM);
	        	 //}
	             invalidate();
	             break;
	         case MotionEvent.ACTION_UP://N.B. se sono con 2 dita, questo non viene lanciato al primo dito rilasciato,ma al secondo
	             touch_up(PINCH_ZOOM);
	             PINCH_ZOOM = false;
	             ApplicationManager.setShowPaintSize(false);
	             //ApplicationManager.refreshGlashPane();
	             invalidate();
	             break;
	     }
	     return true;
	}
    
	private int historicalDistance = -1;
	private void computePinchZoom(MotionEvent event) {
		float Dx = event.getX(0) - event.getX(1);
   	 	float Dy = event.getY(0) - event.getY(1);
   	 	int distance = (int)FloatMath.sqrt(Dx * Dx + Dy * Dy);
   	 
   	 	if(historicalDistance == -1) {
   	 		historicalDistance = distance;
   	 		return;
   	 	}
   	 	
   	 	int paintSize = 0;
   	 	
   	 	/*
   	 	if(ApplicationManager.TOOL_PENNELLO.equalsIgnoreCase(ApplicationManager.getTOOL())) {
   	 		paintSize = ApplicationManager.getPENNELLO_ICON().getPaintSize();
   	 	}else {//GOMMA
   	 		paintSize = ApplicationManager.getGOMMA_ICON().getPaintSize();
   	 	}
   	 	*/
   	 	
   	 	if(distance > historicalDistance) {
	   	 	paintSize +=2;
	 		if(paintSize >= 60) {
	 			paintSize = 60;
	 		}
   	 	}else if(distance < historicalDistance){
	   	 	paintSize -=2;
	 		if(paintSize <= 6) {
	 			paintSize = 6;
	 		}
   	 	}
   	 	
   	 	
   	    historicalDistance = distance;
   	 
   	 	//Applico l'immagine giusta al pennello
   	    //if(ApplicationManager.TOOL_PENNELLO.equalsIgnoreCase(ApplicationManager.getTOOL())) {
   	   /*
   	    if(true) {
   	    	ApplicationManager.getPENNELLO_ICON().setPaintSize(paintSize);
	    	if(paintSize >=6 && paintSize<=10) {
	    		ApplicationManager.getPENNELLO_TEXT().setImageResource(R.drawable.pensize1);
	    	}else if(paintSize >10 && paintSize<=20){
	    		ApplicationManager.getPENNELLO_TEXT().setImageResource(R.drawable.pensize2);
	    	}
			else if(paintSize >20 && paintSize<=30){
				ApplicationManager.getPENNELLO_TEXT().setImageResource(R.drawable.pensize3);
			}
			else if(paintSize >30 && paintSize<=45){
				ApplicationManager.getPENNELLO_TEXT().setImageResource(R.drawable.pensize4);
			}else {
				ApplicationManager.getPENNELLO_TEXT().setImageResource(R.drawable.pensize5);
			}
	    	ApplicationManager.setPaintSize((int)(paintSize*DENSITY +0.5f));
   	    }

   	    if(true){//gomma
   	    	ApplicationManager.getGOMMA_ICON().setPaintSize(paintSize);
   	    	//Applico l'immagine giusta alla gomma
        	if(paintSize >=6 && paintSize<=10) {
        		ApplicationManager.getGOMMA_TEXT().setImageResource(R.drawable.gommasize1);
        	}else if(paintSize >10 && paintSize<=20){
        		ApplicationManager.getGOMMA_TEXT().setImageResource(R.drawable.gommasize2);
        	}
			else if(paintSize >20 && paintSize<=30){
				ApplicationManager.getGOMMA_TEXT().setImageResource(R.drawable.gommasize3);
			}
			else if(paintSize >30 && paintSize<=45){
				ApplicationManager.getGOMMA_TEXT().setImageResource(R.drawable.gommasize4);
			}else {
				ApplicationManager.getGOMMA_TEXT().setImageResource(R.drawable.gommasize5);
			}
        	ApplicationManager.setGommaPaintSize((int)(paintSize*DENSITY +0.5f));
   	    }
	*/
    	ApplicationManager.setShowPaintSize(true);
    	//ApplicationManager.refreshGlashPane();
	}

	public void setColor(int color) {
		mPaint.setColor(color);
		fillPaint.setColor(color);
	}

	
	//Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();
    //Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
	        public void run() {
	        	 resultComputed = true;
	        	 invalidate();
	        	 //Controllo la percentuale
	        	 int rounded = Math.round(picture.computeRelativePercentage((100 - percentage),startingSuccessPercentage));
	        	 //Se � <0 metto a 0%
	        	 if(rounded < 0){
	        		 rounded = 0;
	        	 }
	        	 String percentageResult = ""+rounded;
	        	 dashboardActivity.setResultBitmap(resultBitmap,percentageResult);
	        	 //resultComputed = false;
	        }
    };
    
    Bitmap fullCanvasS;
    public void startResultElaboration() {
    	if(elaborationRunning) return;
    	elaborationRunning = true;
    	isFingerDown = false;
    	
    	//Prima del calcolo concludo il path in corso e lo disegno sulla bitmap
    	//mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        //mPath.reset();
    	
    	fullCanvasS = dashboardActivity.mCanvasView.getBitmap(true);
    	////Log.e("################# startResultElaboration ","startResultElaboration");
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
            	    try {//necessario poich� a volte le bitmap vengono reciclate e questo thread ci lavora sopra
            		//Elaboro
	     	        if(contour != null && colored != null) {
	     	        	if(!resultComputed) {//Per non fare questo calcolo oneroso pi� di una volta
	     	        		
	     	        		//Ridimensiono le bitmap in gioco a una grandezza fissa stabilita (ad esempio 300x300),cosi
	     	        		//velocizziamo l'algoritmo e possiamo cablare il kernel size affinch� funzioni sempre a dovere.
	     	        		//Bitmap scaledMBitmap = Bitmap.createScaledBitmap(mBitmap, ELABORATION_SIZE, ELABORATION_SIZE, false);
	     	        		//SE USO L'S PEN CANVAS A PIENO SCHERMO DEVO RITAGLIARE LA PARTE DI INTERESSE
	     	        		double SW = fullCanvasS.getWidth();
	     	        		double SH = fullCanvasS.getHeight();
	     	        		double sw = getWidth();
	     	        		double sh = getHeight();
	     	        		Bitmap croppedCanvas = Bitmap.createBitmap(fullCanvasS, (int)(SW/2 - sw/2), (int)(SH/2 - sh/2), (int)sw, (int)sh);
	     	        		fullCanvasS.recycle();//gc
	     	        		fullCanvasS = null;//gc
	     	        		
	     	        		Bitmap scaledMBitmap = Bitmap.createScaledBitmap(croppedCanvas, ELABORATION_SIZE, ELABORATION_SIZE, false);
	     	        		Bitmap scaledColored = colored;
	     	        		Bitmap scaledContour = contour;
	     	        		
	     	        		if(scaledMBitmap != croppedCanvas) {
	     	        			croppedCanvas.recycle();//gc
	     	        			croppedCanvas = null;//gc
	     	        		}
	     	        		
	    	 		        //Provo ad analizzare le bitmap per il confronto
	    	 		        //PASSO 1 Unisco il disegno dell'utente con il contorno originale per formare l'immagine finale dell'utente
	    	 		        Bitmap userFinalBitmap = Bitmap.createBitmap(scaledContour.getWidth(), scaledContour.getHeight(), Bitmap.Config.ARGB_8888);
	    	 		        Canvas userResultCanvas = new Canvas(userFinalBitmap);
	    	 		        userResultCanvas.drawBitmap(scaledMBitmap, 0, 0, mResultPaint);
	    	 		        userResultCanvas.drawBitmap(scaledContour, 0, 0, mResultPaint);
	    	 		        //PASSO 2 Creo una nuova bitmap dove disegnare il risultato(errori/fallimenti)
	    	 		        resultBitmap = Bitmap.createBitmap(scaledContour.getWidth(), scaledContour.getHeight(), Bitmap.Config.ARGB_8888);
	    	 		        Canvas resultBitmapCanvas = new Canvas(resultBitmap);
	    	 		        resultBitmapCanvas.drawBitmap(scaledColored, 0, 0, mResultPaint);
	    	 		        //PASSO 3 confronto pixel a pixel le immagini e disegno nel risultato
	    	 		       
	    	 		        int width = scaledColored.getWidth();
	    	 		        int heigth = scaledColored.getHeight();
	    	 		        
	    	 		        int[][] mask = new int[width][heigth];
	    	 		        
	    	 		        for(int w=0; w<width; w++) {
	    	 		        	for(int h=0; h<heigth; h++) {
	    	 		        		//Lascio una soglia di tolleranza minima
	    	 		        		//e controllo se il colore � presente in un intorno di NxN pixel per gli errori di confronto
	    	 		        		//dovuti alle zone in sovrapposizione vicino ai bordi.
	    	 		        		if(ColorUtils.compareColorArea(userFinalBitmap,scaledColored,w,h)) {
	    	 		        			//resultBitmap.setPixel(w, h, Color.GREEN);
	    	 		        			mask[w][h] = 0;
	    	 		        		}else {
	    	 		        			//resultBitmap.setPixel(w, h, Color.RED);
	    	 		        			mask[w][h] = 1;
	    	 		        		}
	    	 		        	}
	    	 		        }
	    	 		        
	    	 		        int[][] erodedMask = FilterUtils.intorno(mask);
	    	 		        int countErrors = 0;
	    	 		        
	    	 		        for(int w=0; w<width; w++) {
	    	 		        	for(int h=0; h<heigth; h++) {
	    	 		        		if(erodedMask[w][h] == 0) {
	    	 		        			//resultBitmap.setPixel(w, h, Color.GREEN);
	    	 		        			//mBitmapPaint.setColor(Color.GREEN);
	    	 		        			//mBitmapPaint.setAlpha(255);
	    	 		        			//resultBitmapCanvas.drawCircle(w, h, 1, mBitmapPaint);
	    	 		        		}else {
	    	 		        			mResultPaint.setColor(Color.RED);
	    	 		        			//mResultPaint.setAlpha(5);
	    	 		        			//resultBitmapCanvas.drawCircle(w, h, 2, mResultPaint);
	    	 		        			resultBitmap.setPixel(w, h, Color.RED);
	    	 		        			countErrors++;
	    	 		        		}
	    	 		        	}
	    	 		        }
	    	 		        //Sul risultato finale sovrappongo il contorno
	    	 		        mResultPaint.setAlpha(255);
	    	 		        resultBitmapCanvas.drawBitmap(scaledContour, 0, 0, mResultPaint);
	    	 		        
	    	 		        //Calcolo la percentuale di errore
	    	 		        int totalPixel = width * heigth;
	    	 		        percentage = (float)(100 * countErrors)/(float)totalPixel;
	    	 		        
	    	 		        //Calcolo la percentuale iniziale di errore
	    	 		        startingSuccessPercentage = PictureBean.computeStartingSuccessPercentage(scaledContour,scaledColored);
	    		    	    //Log.e("#################startingSuccessPercentage ",""+startingSuccessPercentage);
	    		    	    
	    		    	    //Per mostrare il lavoro dell'utente
	    		    	    //resultBitmap = userFinalBitmap;
	    		    	  
	    		    	    mHandler.post(mUpdateResults);
	    		    	    elaborationRunning = false;
	    		    	    
	    		    	    //Rilascio le bitmpap temporanee
	    		    	    if(scaledMBitmap != croppedCanvas) {
	    		    	    	scaledMBitmap.recycle();
	    		    	    	scaledMBitmap = null;
	    		    	    }else {
	    		    	    	croppedCanvas.recycle();
	    		    	    	croppedCanvas = null;
	    		    	    }
	    		    	    //scaledColored.recycle();
	    		    	    //scaledContour.recycle();
	    		    	    userFinalBitmap.recycle();
	    		    	    //scaledColored = null;
	    		    	    //scaledContour = null;
	    		    	    userFinalBitmap = null;
	    		    	    System.gc();
	     	        	}
	     	        }
        	    }catch (Exception e) {
        	    	//Ok siamo nel caso in cui cerchiamo di usare una bitmap che abbiamo gi� reciclato
					e.printStackTrace();
				}
            }
        };
        t.start();
    }

    public void executeAmmo(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
    	
    	if(elaborationRunning) return;
    	
    	if(AmmoManager.SPACEGUN.equalsIgnoreCase(ammo.getName())) {
    		executeSmallBomb(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.POWERSHOT.equalsIgnoreCase(ammo.getName())) {
    		executeBigBomb(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.PLASMANETOR.equalsIgnoreCase(ammo.getName())) {
    		//executeJolly(x,y,context,imageDimension,ammo);
    		executeColorBulletHole(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.DIABLITO.equalsIgnoreCase(ammo.getName())) {
    		executeSkull(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.SMARJE.equalsIgnoreCase(ammo.getName())) {
    		executeInk(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.GNAUZ.equalsIgnoreCase(ammo.getName())) {
    		executeMosca(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.ROCKATTACK.equalsIgnoreCase(ammo.getName())) {
    		executeClusterBomb(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.SHURIKEN.equalsIgnoreCase(ammo.getName())) {
    		executeShanghaiBomb(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.NUCLEAR.equalsIgnoreCase(ammo.getName())) {
    		//executeTriangleBomb(x,y,context,imageDimension,ammo);
    		executeJolly(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.MIX.equalsIgnoreCase(ammo.getName())) {
    		executeSpray(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.BONUSTIME.equalsIgnoreCase(ammo.getName())) {
    		executeMoreSec(x,y,context,imageDimension,ammo,10);
    	}else if(AmmoManager.BJOR.equalsIgnoreCase(ammo.getName())) {
    		executeCrazyColors(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.BOMB.equalsIgnoreCase(ammo.getName())) {
    		executeCandeggina(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.LASERCUT.equalsIgnoreCase(ammo.getName())) {
    		executeMagicEdges(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.SCISSOR.equalsIgnoreCase(ammo.getName())) {
    		executeMagicEdgesInverse(x,y,context,imageDimension,ammo);
    	}else if(AmmoManager.SPINUZ.equalsIgnoreCase(ammo.getName())) {
    		executeBulletHole(x,y,context,imageDimension,ammo);
    	}
    	
    }
    
    Paint ammoPaint = new Paint();
    Paint circlePaint = new Paint();
    //La bomba piccola colora un cerchio grande 70dp.
	private void executeSmallBomb(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		
		//NB imageDimension indica la dimensione del TRICK&TRAP a video, quindi serve per centrare la bomba
		//	 al centro del TRICK&TRAP apparso, altrimenti sta male
		
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap copyOfColoredBitmap = coloredFiltered.copy(config, true);
		Bitmap trasparentBitmap = Bitmap.createBitmap(coloredFiltered.getWidth(), coloredFiltered.getHeight(), config);
		trasparentBitmap.eraseColor(ApplicationManager.TRANSPARENT_COLOR);
		Canvas traspCanvas = new Canvas(trasparentBitmap);
		
		double proportion = (double)mBitmap.getWidth()/(double)copyOfColoredBitmap.getWidth();
		traspCanvas.drawCircle((int)((x+(imageDimension/2))/proportion), (int)((y+(imageDimension/2))/proportion), (int)((imageDimension/2)/proportion), circlePaint);
		Canvas coloredMaskCanvas = new Canvas(copyOfColoredBitmap);
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//dst_out con maschera inversa dovrebbe funzare
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);

	    appendCenteredBitmapOnSCanvas(copyOfColoredBitmap, mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
	    copyOfColoredBitmap.recycle();
        copyOfColoredBitmap=null;
        trasparentBitmap.recycle();
        trasparentBitmap=null;
        System.gc();
	}
	
	//La bomba grande colora un cerchio grande 140dp.
	private void executeBigBomb(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
	
		//NB imageDimension indica la dimensione del TRICK&TRAP a video, quindi serve per centrare la bomba
		//	 al centro del TRICK&TRAP apparso, altrimenti sta male
		
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap copyOfColoredBitmap = coloredFiltered.copy(config, true);
		Bitmap trasparentBitmap = Bitmap.createBitmap(coloredFiltered.getWidth(), coloredFiltered.getHeight(), config);
		trasparentBitmap.eraseColor(ApplicationManager.TRANSPARENT_COLOR);
		Canvas traspCanvas = new Canvas(trasparentBitmap);
		
		double proportion = (double)mBitmap.getWidth()/(double)copyOfColoredBitmap.getWidth();
		traspCanvas.drawCircle((int)((x+(imageDimension/2))/proportion), (int)((y+(imageDimension/2))/proportion), (int)((imageDimension)/proportion), circlePaint);
		Canvas coloredMaskCanvas = new Canvas(copyOfColoredBitmap);
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//dst_out con maschera inversa dovrebbe funzare
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);
	   
	    appendCenteredBitmapOnSCanvas(copyOfColoredBitmap, mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
	    copyOfColoredBitmap.recycle();
        copyOfColoredBitmap=null;
        trasparentBitmap.recycle();
        trasparentBitmap=null;
        System.gc();
	}
	
	//La CLUSTER bomba colora tanti cerchi random in un intorno del punto di pressione.
	private void executeClusterBomb(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap copyOfColoredBitmap = coloredFiltered.copy(config, true);
		Bitmap trasparentBitmap = Bitmap.createBitmap(coloredFiltered.getWidth(), coloredFiltered.getHeight(), config);
		trasparentBitmap.eraseColor(ApplicationManager.TRANSPARENT_COLOR);
		Canvas traspCanvas = new Canvas(trasparentBitmap);
		
		double proportion = (double)mBitmap.getWidth()/(double)copyOfColoredBitmap.getWidth();
		for(int i=0; i<10; i++) {
			
			int randomnumberx = (int)(Math.random()*imageDimension);
			int randomnumbery = (int)(Math.random()*imageDimension);
			
			int randomx =x + randomnumberx;
			int randomy = y + randomnumbery;
			
			traspCanvas.drawCircle((int)(randomx/proportion), (int)(randomy/proportion), (int)((imageDimension/4)/proportion), circlePaint);
		}
		Canvas coloredMaskCanvas = new Canvas(copyOfColoredBitmap);
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//dst_out con maschera inversa dovrebbe funzare
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);

	    appendCenteredBitmapOnSCanvas(copyOfColoredBitmap, mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
	    copyOfColoredBitmap.recycle();
        copyOfColoredBitmap=null;
        trasparentBitmap.recycle();
        trasparentBitmap=null;
        System.gc();
	}
	
	//La X bomba grande colora una X grande quanto il quadro.
	Paint xPaint = new Paint();
	private void executeXBomb(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap copyOfColoredBitmap = coloredFiltered.copy(config, true);
		Bitmap trasparentBitmap = Bitmap.createBitmap(coloredFiltered.getWidth(), coloredFiltered.getHeight(), config);
		trasparentBitmap.eraseColor(ApplicationManager.TRANSPARENT_COLOR);
		Canvas traspCanvas = new Canvas(trasparentBitmap);
		
		//double proportion = (double)mBitmap.getWidth()/(double)copyOfColoredBitmap.getWidth();
		//traspCanvas.drawCircle((int)((x+(imageDimension))/proportion), (int)((y+(imageDimension))/proportion), (int)((imageDimension)/proportion), fillPaint);
		xPaint.setStrokeWidth(copyOfColoredBitmap.getWidth()/5);
		traspCanvas.drawLine(0, 0, copyOfColoredBitmap.getWidth(), copyOfColoredBitmap.getHeight(), xPaint);
		traspCanvas.drawLine(0, copyOfColoredBitmap.getHeight(), copyOfColoredBitmap.getWidth(), 0, xPaint);
		
		Canvas coloredMaskCanvas = new Canvas(copyOfColoredBitmap);
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//dst_out con maschera inversa dovrebbe funzare
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);
	   
	    appendCenteredBitmapOnSCanvas(copyOfColoredBitmap, mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
	    copyOfColoredBitmap.recycle();
        copyOfColoredBitmap=null;
        trasparentBitmap.recycle();
        trasparentBitmap=null;
        System.gc();
	}
	
	//La Shangai bomba disegna tanti bastoncini casuali.
	private void executeShanghaiBomb(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap copyOfColoredBitmap = coloredFiltered.copy(config, true);
		Bitmap trasparentBitmap = Bitmap.createBitmap(coloredFiltered.getWidth(), coloredFiltered.getHeight(), config);
		trasparentBitmap.eraseColor(ApplicationManager.TRANSPARENT_COLOR);
		Canvas traspCanvas = new Canvas(trasparentBitmap);
		
		//double proportion = (double)mBitmap.getWidth()/(double)copyOfColoredBitmap.getWidth();
		//traspCanvas.drawCircle((int)((x+(imageDimension))/proportion), (int)((y+(imageDimension))/proportion), (int)((imageDimension)/proportion), fillPaint);
		xPaint.setStrokeWidth(copyOfColoredBitmap.getWidth()/20);
		for(int i=0; i<10; i++) {
			int x1 = (int)(Math.random()*copyOfColoredBitmap.getWidth());
			int x2 = (int)(Math.random()*copyOfColoredBitmap.getWidth());
			int y1 = (int)(Math.random()*copyOfColoredBitmap.getWidth());
			int y2 = (int)(Math.random()*copyOfColoredBitmap.getWidth());
			traspCanvas.drawLine(x1, y1, x2, y2, xPaint);
		}
		
		Canvas coloredMaskCanvas = new Canvas(copyOfColoredBitmap);
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//dst_out con maschera inversa dovrebbe funzare
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);
	   
	    appendCenteredBitmapOnSCanvas(copyOfColoredBitmap, mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
	    copyOfColoredBitmap.recycle();
        copyOfColoredBitmap=null;
        trasparentBitmap.recycle();
        trasparentBitmap=null;
        System.gc();
	}
	
	//La TRIANGLE bomba grande colora un triangolo grande quanto il quadro.
	private void executeTriangleBomb(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap copyOfColoredBitmap = coloredFiltered.copy(config, true);
		Bitmap trasparentBitmap = Bitmap.createBitmap(coloredFiltered.getWidth(), coloredFiltered.getHeight(), config);
		trasparentBitmap.eraseColor(ApplicationManager.TRANSPARENT_COLOR);
		Canvas traspCanvas = new Canvas(trasparentBitmap);
		
		Path path = new Path();
	    path.setFillType(Path.FillType.EVEN_ODD);
	    path.moveTo(0,0);
	    path.lineTo(0, copyOfColoredBitmap.getHeight());
	    path.lineTo(copyOfColoredBitmap.getWidth(), copyOfColoredBitmap.getHeight());
	    path.lineTo(0,0);
	    path.close();

	    circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		traspCanvas.drawPath(path, circlePaint);
		
		Canvas coloredMaskCanvas = new Canvas(copyOfColoredBitmap);
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//dst_out con maschera inversa dovrebbe funzare
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);

	    appendCenteredBitmapOnSCanvas(copyOfColoredBitmap, mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
        trasparentBitmap.recycle();
        trasparentBitmap=null;
        copyOfColoredBitmap.recycle();
        copyOfColoredBitmap=null;
        System.gc();
	}
	
	//Il JOLLY colora tutta la figura!!!
	private void executeJolly(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		//Rect dst = new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
		//mCanvas.drawBitmap(coloredFiltered, null, dst, mBitmapPaint);
		appendCenteredBitmapOnSCanvas(coloredFiltered,mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
        //invalidate();
	}
	
	//Lo SKULL scolora tutta la figura!!!
	private void executeSkull(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		//mBitmap.eraseColor(ApplicationManager.TRANSPARENT_COLOR);
		//S PEN SDK pulisco il canvas
    	dashboardActivity.mCanvasView.clear();
		//mBitmap = Bitmap.createBitmap(SCREEN_HEIGHT, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
        //invalidate();
	}
	
	//Lo INK disegna sopra la figura una macchia di inchiostro.
	private void executeInk(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		Bitmap inkBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.macchiaink);
		
		int x0 = x + imageDimension/2;
		int y0= y +imageDimension/2;
		
		int X = x0 -imageDimension;
		int Y = y0 -imageDimension;
		
		Bitmap sPencanvasBitmap = dashboardActivity.mCanvasView.getBitmap(true);
		Canvas spenCanvas = new Canvas(sPencanvasBitmap);
		double SW = sPencanvasBitmap.getWidth();
		double SH = sPencanvasBitmap.getHeight();
		double sw = getWidth();
		double sh = getHeight();
		int offSetW = (int)(SW/2 - sw/2);
		int offSetH = (int)(SH/2 - sh/2);
		Rect dst = new Rect(offSetW+X,offSetH+Y,offSetW+X+(imageDimension*2),offSetH+Y+(imageDimension*2));
		spenCanvas.drawBitmap(inkBitmap, null, dst, mBitmapPaint);
		
		dashboardActivity.mCanvasView.setBitmap(sPencanvasBitmap, true);
		//invalidate();
		sPencanvasBitmap.recycle();
		sPencanvasBitmap = null;
		
		inkBitmap.recycle();
		inkBitmap = null;
	}
	
	//La MOSCA se schiacciata disegna lo splat sul disegno.
	private void executeMosca(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		Bitmap inkBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.macchiamosca);
		
		int x0 = x + imageDimension/2;
		int y0= y +imageDimension/2;
		
		int X = x0 -imageDimension;
		int Y = y0 -imageDimension;
		
		Bitmap sPencanvasBitmap = dashboardActivity.mCanvasView.getBitmap(true);
		Canvas spenCanvas = new Canvas(sPencanvasBitmap);
		double SW = sPencanvasBitmap.getWidth();
		double SH = sPencanvasBitmap.getHeight();
		double sw = getWidth();
		double sh = getHeight();
		int offSetW = (int)(SW/2 - sw/2);
		int offSetH = (int)(SH/2 - sh/2);
		Rect dst = new Rect(offSetW+X,offSetH+Y,offSetW+X+(imageDimension*2),offSetH+Y+(imageDimension*2));
		spenCanvas.drawBitmap(inkBitmap, null, dst, mBitmapPaint);
		
		dashboardActivity.mCanvasView.setBitmap(sPencanvasBitmap, true);
		//invalidate();
		sPencanvasBitmap.recycle();
		sPencanvasBitmap = null;
		
		inkBitmap.recycle();
		inkBitmap = null;
	}
	
	//La FOGLIA se schiacciata disegna la foglia sul disegno.
	/*
	private void executeFoglia(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		Bitmap fogliaBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.foglia);
		Rect dst = new Rect(x,y,x+imageDimension,y+imageDimension);
		mCanvas.drawBitmap(fogliaBitmap, null, dst, mBitmapPaint);
		invalidate();
		fogliaBitmap.recycle();
		fogliaBitmap = null;
	}
	*/
	
	//La SPRAY fa una spraiata di pallini sul disegno.
	Paint sprayPaint = new Paint();
	private void executeSpray(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		sprayPaint.setColor(Color.BLACK);
		
		Bitmap sPencanvasBitmap = dashboardActivity.mCanvasView.getBitmap(true);
		Canvas spenCanvas = new Canvas(sPencanvasBitmap);

		int bitmapSizeW = sPencanvasBitmap.getWidth();
		int bitmapSizeH = sPencanvasBitmap.getHeight();
		int meanRadius = bitmapSizeH/200;
		if(meanRadius<=1) meanRadius =2;//cos�...per evitare sorprese
		
		for(int i=0; i<100; i++) {
			int currentRadius = meanRadius + (int)(Math.random()*(meanRadius*2));
			int currentX = (int)(Math.random()*bitmapSizeW);
			int currentY = (int)(Math.random()*bitmapSizeH);
			spenCanvas.drawCircle(currentX, currentY, currentRadius, sprayPaint);
		}
		
		dashboardActivity.mCanvasView.setBitmap(sPencanvasBitmap, true);
		//invalidate();
		sPencanvasBitmap.recycle();
		sPencanvasBitmap = null;
	    System.gc();
	}
	
	//La BULLET HOLE o MITRAGLIATA fa una spraiata di pallini trasparenti (cancella )sul disegno.
	private void executeBulletHole(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		candegginaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
		candegginaPaint.setColor(ApplicationManager.TRANSPARENT_COLOR);
		
		Bitmap sPencanvasBitmap = dashboardActivity.mCanvasView.getBitmap(true);
		Canvas spenCanvas = new Canvas(sPencanvasBitmap);

		int bitmapSizeW = sPencanvasBitmap.getWidth();
		int bitmapSizeH = sPencanvasBitmap.getHeight();
		int meanRadius = bitmapSizeH/50;
		if(meanRadius<=1) meanRadius =2;//cos�...per evitare sorprese
		
		for(int i=0; i<100; i++) {
			int currentRadius = meanRadius + (int)(Math.random()*(meanRadius*2));
			int currentX = (int)(Math.random()*bitmapSizeW);
			int currentY = (int)(Math.random()*bitmapSizeH);
			spenCanvas.drawCircle(currentX, currentY, currentRadius, candegginaPaint);
		}
		
		dashboardActivity.mCanvasView.setBitmap(sPencanvasBitmap, true);
		//invalidate();
		sPencanvasBitmap.recycle();
		sPencanvasBitmap = null;
	    System.gc();
	}
	
	//La color mitra colora tanti pallini casuali
	private void executeColorBulletHole(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap copyOfColoredBitmap = coloredFiltered.copy(config, true);
		Bitmap trasparentBitmap = Bitmap.createBitmap(coloredFiltered.getWidth(), coloredFiltered.getHeight(), config);
		trasparentBitmap.eraseColor(ApplicationManager.TRANSPARENT_COLOR);
		Canvas traspCanvas = new Canvas(trasparentBitmap);

		int bitmapSize = copyOfColoredBitmap.getWidth();
		int meanRadius = bitmapSize/100;
		if(meanRadius<=1) meanRadius =2;//cos�...per evitare sorprese
		
		for(int i=0; i<100; i++) {
			int currentRadius = meanRadius + (int)(Math.random()*(meanRadius*2));
			int currentX = (int)(Math.random()*bitmapSize);
			int currentY = (int)(Math.random()*bitmapSize);
			traspCanvas.drawCircle(currentX, currentY, currentRadius, xPaint);
		}
		
		Canvas coloredMaskCanvas = new Canvas(copyOfColoredBitmap);
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//dst_out con maschera inversa dovrebbe funzare
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);

	    appendCenteredBitmapOnSCanvas(copyOfColoredBitmap, mBitmapPaint,VIEW_HEIGHT,VIEW_HEIGHT);
	    copyOfColoredBitmap.recycle();
        copyOfColoredBitmap=null;
        trasparentBitmap.recycle();
        trasparentBitmap=null;
        System.gc();
	}

	//CRAZY COLORS cambia il colore del pennello con un colore casuale.
	private void executeCrazyColors(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		//Creo un colore casuale
		int colore = Color.rgb((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
		//Cambio lo strumento
    	ApplicationManager.setTOOL(ApplicationManager.TOOL_PENNELLO);
    	ApplicationManager.setCurrentColor(colore);
    	changeSPENColor(colore);
		this.setColor(colore);
	}
	
	//AUMENTA IL TOTALE DEI SECONDI DISPONIBILI.
	private void executeMoreSec(int x,int y,Context context,int imageDimension, AmmoBean ammo, int sec) {
		TimeManager.setTotalTime(TimeManager.getTotalTime() + sec*1000);
	}
	
	//TERREMOTO fa vibrare il quadro.
	private void executeTerremoto(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		 Animation anim = AnimationFactory.getTerremotoAnimation(context);
		 this.startAnimation(anim);
		 dashboardActivity.mCanvasView.startAnimation(anim);
	}
	
	//VORTICE fa ruotare il quadro.
	private void executeVortice(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		 Animation anim = AnimationFactory.getVorticeAnimation(context);
		 this.startAnimation(anim);
		 dashboardActivity.mCanvasView.startAnimation(anim);
	}
	
	//La CANDEGGINA cancella un cerchio dove si � cliccato.
	Paint candegginaPaint = new Paint();
	private void executeCandeggina(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		candegginaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
		candegginaPaint.setColor(ApplicationManager.TRANSPARENT_COLOR);
	   
        Bitmap sPencanvasBitmap = dashboardActivity.mCanvasView.getBitmap(true);
		Canvas spenCanvas = new Canvas(sPencanvasBitmap);

		double SW = sPencanvasBitmap.getWidth();
		double SH = sPencanvasBitmap.getHeight();
		double sw = getWidth();
		double sh = getHeight();
		int offSetW = (int)(SW/2 - sw/2);
		int offSetH = (int)(SH/2 - sh/2);
		
		spenCanvas.drawCircle(offSetW+(int)((x+(imageDimension/2))), offSetH+(int)((y+(imageDimension/2))), (int)((imageDimension)), candegginaPaint);
        
		dashboardActivity.mCanvasView.setBitmap(sPencanvasBitmap, true);
		//invalidate();
		sPencanvasBitmap.recycle();
		sPencanvasBitmap = null;
	    System.gc();

	}
	
	//La MAGIC EDGES pulisce tutto ci� che � fuori dai bordi dell'immagine.
	private void executeMagicEdges(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		//Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap trasparentBitmap = Bitmap.createScaledBitmap(coloredFiltered, getWidth(), getHeight(), false);
		Bitmap copyOfColoredBitmap = dashboardActivity.mCanvasView.getBitmap(true);
		
		//Ritaglio la parte di interesse
		double SW = copyOfColoredBitmap.getWidth();
 		double SH = copyOfColoredBitmap.getHeight();
 		double sw = getWidth();
 		double sh = getHeight();
 		Bitmap croppedCanvas = Bitmap.createBitmap(copyOfColoredBitmap, (int)(SW/2 - sw/2), (int)(SH/2 - sh/2), (int)sw, (int)sh);
 		copyOfColoredBitmap.recycle();//gc
 		copyOfColoredBitmap = null;//gc
 		
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//dst_out con maschera inversa dovrebbe funzare
	    //Rect dst = new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
	    //mCanvas.drawBitmap(trasparentBitmap, null, dst, ammoPaint);
        //invalidate();
	    Canvas coloredMaskCanvas = new Canvas(croppedCanvas);
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);
	    
	    dashboardActivity.mCanvasView.clear();
	    appendCenteredBitmapOnSCanvas(croppedCanvas, mBitmapPaint, VIEW_WIDTH, VIEW_WIDTH);
	    
 		croppedCanvas.recycle();//gc
 		croppedCanvas = null;//gc
 		if(trasparentBitmap != coloredFiltered) {
 			trasparentBitmap.recycle();
        	trasparentBitmap=null;
 		}
        System.gc();
	}
	
	//La MAGIC EDGES pulisce tutto ci� che � fuori dai bordi dell'immagine.
	private void executeMagicEdgesInverse(int x,int y,Context context,int imageDimension, AmmoBean ammo) {
		//Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap trasparentBitmap = Bitmap.createScaledBitmap(coloredFiltered, getWidth(), getHeight(), false);
		Bitmap copyOfColoredBitmap = dashboardActivity.mCanvasView.getBitmap(true);
		
		//Ritaglio la parte di interesse
		double SW = copyOfColoredBitmap.getWidth();
 		double SH = copyOfColoredBitmap.getHeight();
 		double sw = getWidth();
 		double sh = getHeight();
 		Bitmap croppedCanvas = Bitmap.createBitmap(copyOfColoredBitmap, (int)(SW/2 - sw/2), (int)(SH/2 - sh/2), (int)sw, (int)sh);
 		copyOfColoredBitmap.recycle();//gc
 		copyOfColoredBitmap = null;//gc
 		
	    ammoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));//dst_out con maschera inversa dovrebbe funzare
	    //Rect dst = new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
	    //mCanvas.drawBitmap(trasparentBitmap, null, dst, ammoPaint);
        //invalidate();
	    Canvas coloredMaskCanvas = new Canvas(croppedCanvas);
	    coloredMaskCanvas.drawBitmap(trasparentBitmap, 0, 0, ammoPaint);
	    
	    dashboardActivity.mCanvasView.clear();
	    appendCenteredBitmapOnSCanvas(croppedCanvas, mBitmapPaint, VIEW_WIDTH, VIEW_WIDTH);
	    
 		croppedCanvas.recycle();//gc
 		croppedCanvas = null;//gc
 		if(trasparentBitmap != coloredFiltered) {
 			trasparentBitmap.recycle();
        	trasparentBitmap=null;
 		}
        System.gc();
	}
	
	
	public void setResultComputed(boolean b) {
		this.resultComputed = b;
	}
	
	public void changeSPENMode(int mode) {
		dashboardActivity.mCanvasView.setCanvasMode(mode);
		//La dimensione � la stessa per ogni modalit�
		SettingStrokeInfo strokeInfo = dashboardActivity.mCanvasView.getSettingViewStrokeInfo();
		if(strokeInfo != null) {
			strokeInfo.setStrokeWidth(ApplicationManager.getPaintSize());
			dashboardActivity.mCanvasView.setSettingViewStrokeInfo(strokeInfo);	
		}
	}
	
	public void changeSPENColor(int color) {
    	/*
		PenSettingInfo info = dashboardActivity.mCanvasView.getPenSettingInfo();
        info.setPenColor(color);
        */
        
        SettingStrokeInfo strokeInfo = dashboardActivity.mCanvasView.getSettingViewStrokeInfo();
		if(strokeInfo != null) {
			strokeInfo.setStrokeColor(color);
			dashboardActivity.mCanvasView.setSettingViewStrokeInfo(strokeInfo);	
		}	
		
		
        //dashboardActivity.mCanvasView.setPenSettingInfo(info);
    }
	
	public void changeSPENSize(int size) {
		/*
    	PenSettingInfo info = dashboardActivity.mCanvasView.getPenSettingInfo();
        info.setPenWidth(size);
        info.setEraserWidth(size);
        //dashboardActivity.mCanvasView.setPenSettingInfo(info);
        */
        
		SettingStrokeInfo strokeInfo = dashboardActivity.mCanvasView.getSettingViewStrokeInfo();
		if(strokeInfo != null) {
			strokeInfo.setStrokeWidth(size);
			dashboardActivity.mCanvasView.setSettingViewStrokeInfo(strokeInfo);	
		}
			
    }
}
