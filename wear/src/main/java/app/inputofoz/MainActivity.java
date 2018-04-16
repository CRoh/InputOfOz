package app.inputofoz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


/**
 * Diese Klasse ist verantwortlich für die Anzeige/Darstellung der App und die Interaktion mit dem Benutzer.
 */
public class MainActivity extends WearableActivity {

	/**
	 * Der ImageView, welcher das Symbol auf der Uhr anzeigt.
	 */
	private ImageView imageView;

	/**
	 * Verbindung zu den Google Cloud Services, benötigt für Wearable.DataApi.
	 */
	private GoogleApiClient mGoogleApiClient;

	/**
	 * Einstieg in die MainActivity der Uhr-App.
	 * Lädt das Layout der App aus der Datei activity_main.
	 * Stellt die Verbindung zu den Google Cloud Services her.
	 * Zeigt standardmäßig das Symbol "image_0.jpg" im imageView an.
	 * Registriert den mMessageReceiver für den LocalBroadcastManager im DataListenerService,
	 * so dass eine Kommunikation zwischen beiden Komponenten stattfinden kann.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//---Build a new Google API client---
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API)
				.build();
		mGoogleApiClient.connect();

		imageView = (ImageView) findViewById(R.id.imageView);
		setNextImage(0);


		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter("ImageReceivedIntent"));

		// Lässt die Uhr dauerhaft anbleiben und nicht aus Versehen das Display abdunkeln.
		setAmbientEnabled();
	}

	/**
	 * Innere anonyme Klasse, welche auf den LocalBroadcastManager horcht, welcher im DataListenerService
	 * benutzt wird um der MainActivity eine neue Zufallszahl mitzuteilen.
	 */
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		/**
		 * Wird aufgerufen, wenn der LocalBroadcastManager eine neue Zufallszahl sendet.
		 * Diese Methode sorgt dafür, dass anschließend das Symbol im imageView angezeigt wird,
		 * durch Aufruf der Methode setNextImage().
		 *
		 * @param intent Enthält die Zufallszahl welche der DataListenerService aus den CGS bekommen hat.
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			int number = intent.getIntExtra("nextImageNumber", -1);
			MainActivity.this.setNextImage(number);
		}
	};

	/**
	 * Lädt das Symbol mit der angegebenen Nummer und zeigt es im imageView an.
	 * Lässt haptisches Feedback abspielen.
	 *
	 * @param number Die Nummer des Symbols, welches angezeigt werden soll.
	 */
	private void setNextImage(int number) {
		int currentImage = R.drawable.image_0;
		switch (number) {
			case 0: currentImage = R.drawable.image_0; break;
			case 1: currentImage = R.drawable.image_1; break;
			case 2: currentImage = R.drawable.image_2; break;
			case 3: currentImage = R.drawable.image_3; break;
		}

		vibrate();
		imageView.setImageResource(currentImage);
	}

	/**
	 * Lässt die Uhr eine halbe Sekunde lang haptisches Feedback wiedergeben.
	 */
	private void vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		long[] vibrationPattern = {0, 500};
		//-1 - don't repeat
		final int indexInPatternToRepeat = -1;
		vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
	}
}
