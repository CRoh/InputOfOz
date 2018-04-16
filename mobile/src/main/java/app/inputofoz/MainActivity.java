package app.inputofoz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;
import java.util.Random;

/**
 * Diese Klasse ist verantwortlich für die Anzeige/Darstellung der App und die Interaktion mit dem Benutzer.
 */
public class MainActivity extends AppCompatActivity {

	/**
	 * Der ImageView, welcher das Symbol auf dem Handy anzeigt.
	 */
	private ImageView imageView;

	/**
	 * Verbindung zu den Google Cloud Services, benötigt für Wearable.DataApi.
	 */
	private GoogleApiClient mGoogleApiClient;

	/**
	 * Wird mitgespeichert um sicherzustellen, dass nicht zweimal die gleiche Zufallszahl hintereinander generiert wird.
	 */
	private int currentRandomNumber = 0;

	/**
	 * Anzahl der Symbole, benötigt zur einfachen Generierung einer neuen Zufallszahl.
	 */
	private static int numberOfImages = 4;

	/**
	 * Einstegspunkt in die Activity.
	 * Lädt das Layout aus der Datei activity_main.
	 * Stellt die Verbindung zum Google Cloud Services Client her.
	 * Konfiguriert die OnClick-Action für den Button.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imageView = (ImageView) findViewById(R.id.imageView);
		setNextImage();

		connectGoogleApiClient();

		Button button = (Button) findViewById(R.id.button);
		// Button wird gedrückt -> Zufallszahl wird generiert und in die Cloud Services geschickt.
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setNextImage();


				PutDataMapRequest dataMap = PutDataMapRequest.create("/data");
				dataMap.getDataMap().putInt("showNextImage", currentRandomNumber);
				dataMap.getDataMap().putLong("timeStamp", new Date().getTime()); // Stellt sicher, dass die Daten immer neu hochgeladen werden.

				PutDataRequest request = dataMap.asPutDataRequest();

				PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
			}
		});
	}

	/**
	 * Stellt die Verbindung zu den Google Cloud Services her.
	 * Fügt die Wearable.DataApi hinzu, welche für die Kommunikation mit der Uhr benötigt wird.
	 */
	private void connectGoogleApiClient() {
		//---Build a new Google API client---
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API)
				.build();
		mGoogleApiClient.connect();
	}

	/**
	 * Lässt eine neue Zufallszahl generieren und setzt das entsprechende Symbol im imageView.
	 */
	private void setNextImage() {
		generateNewRandomNumber();

		int currentImage = R.drawable.image_0;
		switch (currentRandomNumber) {
			case 0: currentImage = R.drawable.image_0; break;
			case 1: currentImage = R.drawable.image_1; break;
			case 2: currentImage = R.drawable.image_2; break;
			case 3: currentImage = R.drawable.image_3; break;
		}

		imageView.setImageResource(currentImage);
	}

	/**
	 * Das eigentliche Generieren einer neuen Zufallszahl.
	 * Wird solange ausgeführt, bis die neue Zahl ungleich der vorherigen ist.
	 */
	private void generateNewRandomNumber() {
		Random generator = new Random();
		int newRandomNumber = generator.nextInt(numberOfImages);

		if (currentRandomNumber == newRandomNumber) {
			generateNewRandomNumber();
		} else {
			currentRandomNumber = newRandomNumber;
		}
	}
}
