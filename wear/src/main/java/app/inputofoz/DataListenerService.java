package app.inputofoz;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Der DataListenerService läuft auf der Uhr im Hintergrund und horcht auf eingehende Nachrichten des Google Cloud Services.
 */
public class DataListenerService extends WearableListenerService {

	/**
	 * Diese Methode wird automatisch vom System aufgerufen wenn eine neue Nachricht vom Google Cloud Service eintrifft.
	 * Diese Nachricht liefert in unserem Fall die vom Handy generierte Zufallszahl mit.
	 * Anschließend wird diese Zufallszahl per LocalBroadcastManager an die MainActivity der Uhr-App geschickt.
	 *
	 * @param dataEvents Enthält Daten und Informationen aus den GCS, in unserem Fall einen int-Wert.
	 */
	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		for (DataEvent event : dataEvents) {
			if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().equals("/data")) {
				DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
				int nextImageNumber = dataMapItem.getDataMap().getInt("showNextImage"); // Liest die Zufallszahl aus

				Intent intent = new Intent("ImageReceivedIntent");
				intent.putExtra("nextImageNumber", nextImageNumber);

				LocalBroadcastManager.getInstance(DataListenerService.this).sendBroadcast(intent);
			}
		}
	}
}
