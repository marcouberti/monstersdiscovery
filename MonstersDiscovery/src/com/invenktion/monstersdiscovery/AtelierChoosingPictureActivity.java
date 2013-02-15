package com.invenktion.monstersdiscovery;

import java.util.ArrayList;

import com.invenktion.monstersdiscovery.ArcadeModeChooseLevelActivity.ImageAdapter;
import com.invenktion.monstersdiscovery.bean.PictureBean;
import com.invenktion.monstersdiscovery.bean.SectionArrayList;
import com.invenktion.monstersdiscovery.core.AnimationFactory;
import com.invenktion.monstersdiscovery.core.ApplicationManager;
import com.invenktion.monstersdiscovery.core.FontFactory;
import com.invenktion.monstersdiscovery.core.LevelManager;
import com.invenktion.monstersdiscovery.core.SoundManager;
import com.invenktion.monstersdiscovery.R;
import com.invenktion.monstersdiscovery.utils.ActivityHelper;
import com.invenktion.monstersdiscovery.utils.LogUtils;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenHoverListener;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class AtelierChoosingPictureActivity extends Activity{
	
	float DENSITY = 1.0f;
	Gallery g;
	ImageAdapter adapter;
	private boolean waiting = false;
	//int selectedPosition = -1;
	//int scrolledPosition = 0;
	
	@Override
	protected void onPause() {
		super.onPause();
		//Spengo la musica solo se un'altra applicazione è davanti alla nostra (VOICE CALL, HOME Button, etc..)
		if(ActivityHelper.isApplicationBroughtToBackground(this)) {
			SoundManager.pauseBackgroundMusic();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(waiting) return false;
	    	else {
	    		waiting = true;
	    		finish();
	    		overridePendingTransition(0,0);
	        	return true;
	    	}
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//if(glassPane != null) {
			//glassPane.recycleBitmaps();
		//}
		LevelManager.clearAllCachedImage();
		//Log.e("AtelierChoosingPictureActivity","DESTROY AtelierChoosingPictureActivity ####################");
		//System.gc();
	}
	
	//Questo viene chiamato quando l'utente clicca il tasto "back" e dal livello torna qui
	@Override
	protected void onResume() {
		super.onResume();
		//Aggiorno la gallery con gli eventuali nuovi risultati.
		adapter.notifyDataSetChanged();

		//Rilancio la musica se e solo se non è già attiva
		//Questo ci permette di utilizzare la stessa traccia musicale tra Activity differenti, oltre
		//al metodo presente nel onPause che controlla se siamo o no in background
		KeyguardManager keyguardManager = (KeyguardManager)getApplicationContext().getSystemService(Activity.KEYGUARD_SERVICE);  
    	boolean bloccoSchermoAttivo = keyguardManager.inKeyguardRestrictedInputMode();
		if(!bloccoSchermoAttivo && !SoundManager.isBackgroundMusicPlaying()) {
			SoundManager.playBackgroundMusic(getApplicationContext());
		}
		waiting = false;
	}

	private boolean checkApplicationKill() {
		if(ApplicationManager.APPLICATION_KILLED == null) {
			finish();
			return true;
		}
		return false;
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean finish = checkApplicationKill();
        if(finish) return;
        this.DENSITY = getApplicationContext().getResources().getDisplayMetrics().density;
        setContentView(R.layout.atelier);
        
        
        ImageView mascotteImage = (ImageView)findViewById(R.id.mascotteimage);
        mascotteImage.setLayoutParams(new LinearLayout.LayoutParams((int)(ApplicationManager.SCREEN_H/2.5), (int)(ApplicationManager.SCREEN_H/2.5)));
        
        final TextView scrollText = (TextView)findViewById(R.id.scrolltext);
        
        scrollText.setTextAppearance(getApplicationContext(), R.style.TimeFont_Black_NumberScroll);
        scrollText.setTypeface(FontFactory.getFont1(getApplicationContext()));
        
        Button prevBut = (Button)findViewById(R.id.prevbut);
        Button nextBut = (Button)findViewById(R.id.nextbut);
        prevBut.setSoundEffectsEnabled(false);
        nextBut.setSoundEffectsEnabled(false);
        nextBut.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				int nextpos = g.getSelectedItemPosition()+1;
				if(nextpos >g.getCount()-1) nextpos = g.getCount()-1;
				g.setSelection(nextpos);
			}
		});
        prevBut.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				int nextpos = g.getSelectedItemPosition()-1;
				if(nextpos <0) nextpos = 0;
				g.setSelection(nextpos);
			}
		});
        
        adapter = new ImageAdapter(getApplicationContext());
        
        g = (Gallery)findViewById(R.id.atelierlistview);
        //g.setKeepScreenOn(true);
        g.setAdapter(adapter);
        g.setSoundEffectsEnabled(false);
        g.setSpacing((int)(10*DENSITY+0.5f));//spazio tra le immagini della gallery
        g.setAnimationDuration(500);
        g.setAnimation(null);
        g.setAnimationCacheEnabled(true);
        g.setDrawingCacheEnabled(true);
        g.setHorizontalFadingEdgeEnabled(false);
        g.setUnselectedAlpha(255);
        
        g.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//scrolledPosition = arg2;
				scrollText.setText(arg2+1 + " / "+ g.getCount());
				LogUtils.logHeap();
			}
			
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
        
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, final int position, long id) {
            	if(waiting) return;
            	waiting = true;
            	
            	PictureBean pic = adapter.getItem(position);
				//selectedPosition = position;
				LevelManager.setCurrentSection(pic.getSectionIndex());
				LevelManager.setCurrentLevelIndex(pic.getLevelIndex());
            	final boolean isBlocked = pic.isBlocked(getApplicationContext(), "arcade");
            	
				if(!(isBlocked)) {
					//Salvo nelle shared preferences questo livello
            		new Thread() {
            			public void run() {
                		SharedPreferences settings = getApplicationContext().getSharedPreferences(ApplicationManager.PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("last_atelier_picture_used", position);
                        //Commit the edits!
                        editor.commit();
            			}
            		}.start();
        		
            		Intent myIntent = new Intent(AtelierChoosingPictureActivity.this, DrawChallengeActivity.class);
            		myIntent.putExtra("gamemode", ApplicationManager.ATELIER);
            		AtelierChoosingPictureActivity.this.startActivity(myIntent);
            		overridePendingTransition(0,0);
				}else {
					waiting = false;
				}
            }
        });
        
        //Mi posiziono sull'ultimo quadro usato nell'atelier, leggendo dalle shared preferences
        SharedPreferences settings = getApplicationContext().getSharedPreferences(ApplicationManager.PREFS_NAME, Context.MODE_PRIVATE);
		int lastUsed =  settings.getInt("last_atelier_picture_used", 0);
		if(lastUsed < g.getCount()) {
			g.setSelection(lastUsed);
		}
    }
    private SPenEventLibrary sPenEventLibrary = new SPenEventLibrary();
    private View buildView(final PictureBean pic, int position) {
		//FrameLayout frameLayout = (FrameLayout)findViewById(R.id.arcadechooselevellayout);
		//Facciamo due strisce con 5 livelli ciscuna
        final LinearLayout container = new LinearLayout(getApplicationContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout primariga = new LinearLayout(getApplicationContext());
        primariga.setOrientation(LinearLayout.HORIZONTAL);
        primariga.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout secondariga = new LinearLayout(getApplicationContext());
        secondariga.setOrientation(LinearLayout.HORIZONTAL);
        secondariga.setGravity(Gravity.CENTER_HORIZONTAL);
        container.addView(primariga);
        container.addView(secondariga);
       
        Resources res;
    	Options opts;
    	res = getApplicationContext().getResources();
    	opts = new BitmapFactory.Options();
    	opts.inSampleSize=2;
    	Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    	opts.inPreferredConfig = conf;
       
    	int currentSectionTela = R.drawable.tela;
        int currentSectionFront = R.drawable.fronte;
    	
        //Prelevo la tela corretta in base alla sezione di appartenenza
    	ArrayList<SectionArrayList> sezioni = LevelManager.getAllSections();
    	int picSectionIndex = pic.getSectionIndex();
    	SectionArrayList<PictureBean> sezioneCorrente = null;
    	if(picSectionIndex != (sezioni.size() -1)) {//ossia l'atelier
    		sezioneCorrente = sezioni.get(picSectionIndex);
    	}
    	
    	if(sezioneCorrente != null) {
    		currentSectionTela = sezioneCorrente.getTelaImage();
    		currentSectionFront =sezioneCorrente.getTelaFrontImage();
    	}
    	//fine prelevo tela corretta

        	final LinearLayout ll = new LinearLayout(getApplicationContext());
        	ll.setOrientation(LinearLayout.VERTICAL);
        	ll.setGravity(Gravity.TOP);
        	//ll.setBackgroundColor(Color.WHITE);
        	//ll.setBackgroundResource(R.drawable.dialogbg);
        	
        	final ImageView i = new ImageView(getApplicationContext());
        	final ImageView iBg = new ImageView(getApplicationContext());
        	final ImageView iFg = new ImageView(getApplicationContext());

        	//Controllo se il livello è stato già sbloccato o meno
            boolean isUnlocked = !(pic.isBlocked(getApplicationContext(), "arcade"));
            //boolean isUnlocked = true;
            
            float scale = AtelierChoosingPictureActivity.this.DENSITY;

        	//int Hpic = (int)(70*scale+0.5f);
        	//int H = (int)(150*scale+0.5f);
            int pixelCorrispondenti = (int)(ApplicationManager.LEVEL_GALLERY_MAX_APPARENT_SIZE_DP*DENSITY+0.5f);
        	int H = (int)(ApplicationManager.SCREEN_W*0.3);
            if(H > pixelCorrispondenti) {
            	H = pixelCorrispondenti;
            }
            int Hpic = (int)((double)H*0.6);
 
        	//opts.inPurgeable=true;
        	if(isUnlocked) {//La prima è sbloccata di default
        		//i.setImageBitmap(BitmapFactory.decodeResource(res, pic.getColoredPicture(),opts));
        		i.setImageBitmap(pic.getColoredPicture(getApplicationContext()));
        		i.setLayoutParams(new LinearLayout.LayoutParams(Hpic,Hpic));
        		i.setScaleType(ImageView.ScaleType.FIT_XY);
        	}
        	if(isUnlocked) {//La prima è sbloccata di default
        		iBg.setImageResource(currentSectionTela);
        		iFg.setImageResource(currentSectionFront);
        	}else{
        		iBg.setImageResource(R.drawable.tela_coperta);
        		iFg.setImageResource(R.drawable.transparent);
        	}
            iBg.setLayoutParams(new FrameLayout.LayoutParams(H,H));
            iBg.setScaleType(ImageView.ScaleType.FIT_XY);
            
            iFg.setLayoutParams(new FrameLayout.LayoutParams(H,H));
            iFg.setScaleType(ImageView.ScaleType.FIT_XY);
         
            LinearLayout imagell = new LinearLayout(getApplicationContext());
            imagell.setGravity(Gravity.CENTER_HORIZONTAL);
	            RelativeLayout relativeLayoutImage = new RelativeLayout(getApplicationContext());
	            relativeLayoutImage.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,Gravity.CENTER_HORIZONTAL));
	            	relativeLayoutImage.addView(iBg);
	            	if(isUnlocked) {//La prima è sbloccata di default
	            		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(Hpic, Hpic);
	            		relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
	            		relativeLayoutImage.addView(i,relativeParams);
	            	}
	            	relativeLayoutImage.addView(iFg);
            imagell.addView(relativeLayoutImage);
            
            final LinearLayout starsll = new LinearLayout(getApplicationContext());
            ImageView starsImage1 = new ImageView(getApplicationContext());
            ImageView starsImage2 = new ImageView(getApplicationContext());
            ImageView starsImage3 = new ImageView(getApplicationContext());
            starsll.addView(starsImage1);
            starsll.addView(starsImage2);
            starsll.addView(starsImage3);
            //Le stelle le facciamo larghe 1/5.5 del quadro
            int sizeStar = (int)((double)H/5.5);
            starsImage1.setLayoutParams(new LinearLayout.LayoutParams(sizeStar,sizeStar));
            starsImage2.setLayoutParams(new LinearLayout.LayoutParams(sizeStar,sizeStar));
            starsImage3.setLayoutParams(new LinearLayout.LayoutParams(sizeStar,sizeStar));
            starsll.setGravity(Gravity.CENTER_HORIZONTAL);
        
            if(isUnlocked) {//La prima è sbloccata di default
	            //Leggo il risultato migliore ottenuto fino ad ora
	            int bestResult = pic.getBestResultEver(getApplicationContext(), ApplicationManager.ATELIER);
	            //Leggo l'ultimo risultato ottenuto
	            int lastResult = pic.getLastResult(getApplicationContext(), ApplicationManager.ATELIER);
	     
	            if(bestResult >= ApplicationManager.THREE_STAR_PERCENTAGE) {
	            	starsImage1.setImageResource(R.drawable.stella_black);
	            	starsImage2.setImageResource(R.drawable.stella_black);
	            	starsImage3.setImageResource(R.drawable.stella_black);
	            }else if(bestResult >= ApplicationManager.TWO_STAR_PERCENTAGE) {
	            	starsImage1.setImageResource(R.drawable.stella_black);
	            	starsImage2.setImageResource(R.drawable.stella_black);
	            	starsImage3.setImageResource(R.drawable.stella_black_tr);
	            }else if(bestResult >= ApplicationManager.ONE_STAR_PERCENTAGE) {
	            	starsImage1.setImageResource(R.drawable.stella_black);
	            	starsImage2.setImageResource(R.drawable.stella_black_tr);
	            	starsImage3.setImageResource(R.drawable.stella_black_tr);
	            }else {
	            	starsImage1.setImageResource(R.drawable.stella_black_tr);
	            	starsImage2.setImageResource(R.drawable.stella_black_tr);
	            	starsImage3.setImageResource(R.drawable.stella_black_tr);
	            }
            }else {
	            starsImage1.setImageResource(R.drawable.stella_black_tr);
            	starsImage2.setImageResource(R.drawable.stella_black_tr);
            	starsImage3.setImageResource(R.drawable.stella_black_tr);
            }
            
            TextView title = new TextView(getApplicationContext());
            title.setText(pic.getTitle(getApplicationContext()));

            title.setTextAppearance(getApplicationContext(), R.style.GalleryTitleText_Nero);
            title.setTypeface(FontFactory.getFont1(getApplicationContext()));
            title.setGravity(Gravity.CENTER_HORIZONTAL);
            
            ll.addView(starsll);
            ll.addView(imagell);
            ll.addView(title);
           
            primariga.addView(ll);

        //HOVER EVENT S PEN SDK
        sPenEventLibrary.setSPenHoverListener(i,new SPenHoverListener(){
        	int W = -1;
        	int H = -1;
			public boolean onHover(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_HOVER_ENTER)	{
					if(W == -1){
						W = i.getWidth();
						H = i.getHeight();
					}
					//starsll.setVisibility(View.INVISIBLE);
					//Carico l'animazioncina
					Animation hoverEnterAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hoverenteranimation);
					hoverEnterAnimation.setFillAfter(true);
					i.startAnimation(hoverEnterAnimation);
					i.setImageResource(pic.getColoredPicture());
					
					iBg.setVisibility(View.INVISIBLE);
					iFg.setVisibility(View.INVISIBLE);
					
					/*
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)(W*2.5), (int)(H*2.5));
					params.addRule(RelativeLayout.CENTER_IN_PARENT);
					i.setLayoutParams(params);
					i.setImageResource(pic.getColoredPicture());
					*/
				}else if(event.getAction()==MotionEvent.ACTION_HOVER_EXIT){
					//Carico l'animazioncina
					//Animation hoverExitAnimation = AnimationFactory.getHoverExitAnimation(getApplicationContext());
					//hoverExitAnimation.setFillAfter(true);
					//i.setAnimation(null);
					i.setAnimation(null);
					//starsll.setVisibility(View.VISIBLE);
					if(W == -1){
						W = i.getWidth();
						H = i.getHeight();
					}
					
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)(W), (int)(H));
					params.addRule(RelativeLayout.CENTER_IN_PARENT);
					i.setLayoutParams(params);
					
					iBg.setVisibility(View.VISIBLE);
					iFg.setVisibility(View.VISIBLE);
				}
				return true;
			}

			public void onHoverButtonDown(View arg0, MotionEvent arg1) {

			}

			public void onHoverButtonUp(View arg0, MotionEvent arg1) {

			}
        });
            
        return container;
	}
    
    //Adapter per la Gallery
    class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;
        Resources res;
    	Options opts;

        private ArrayList<PictureBean> allLevels = new ArrayList<PictureBean>();

        public ImageAdapter(Context c) {
            mContext = c;
            res = mContext.getResources();
        	opts = new BitmapFactory.Options();
        	opts.inSampleSize=2;
        	Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        	opts.inPreferredConfig = conf;
        	
        	//inserisco nella lista tutti i livelli
        	ArrayList<SectionArrayList> sezioni = LevelManager.getAllSections();
        	
        	//Prima metto quelli di prova dell'Atelier, che sono sempre sbloccati
        	int levContAtelier = 0;
        	for(PictureBean bean: LevelManager.getAtelierTrialSection()) {
        		bean.setSectionIndex(sezioni.size() - 1);
    			bean.setLevelIndex(levContAtelier);
        		allLevels.add(bean);
        		levContAtelier++;
    		}
        	
        	int cont = 0;
        	//L'ultima sezione è atelier, non la rimetto
        	for(SectionArrayList<PictureBean> s: sezioni) {
        		if(cont >= (sezioni.size() -1)) break;
        		int levCont = 0;
        		for(PictureBean bean: s) {
        			bean.setSectionIndex(cont);
        			bean.setLevelIndex(levCont);
        			allLevels.add(bean);
        			levCont++;
        		}
        		cont++;
        	}
        }

        public int getCount() {
        	int div = allLevels.size();

            return div;
        }

        public PictureBean getItem(int position) {
            return allLevels.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	return buildView(allLevels.get(position), position);
        }
    }
}
