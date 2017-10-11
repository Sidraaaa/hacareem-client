package sidra.kmlaplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
	
	
	SupportMapFragment mMapFrag;
	GoogleMap mMap;
	KmlLayer kmlLayer;
	Marker mCurrLocationMarker;
	
	
	LocationManager locationManager;
	LocationListener locationListener = new LocationListener()
	{
		@Override
		public void onLocationChanged( Location location )
		{
			
			double lattitude = location.getLatitude();
			double longitude = location.getLongitude();
			
			//Place current location marker
			LatLng latLng = new LatLng( lattitude, longitude );
			
			
			if ( mCurrLocationMarker != null )
			{
				mCurrLocationMarker.setPosition( latLng );
			}
			else
			{
				mCurrLocationMarker = mMap.addMarker( new MarkerOptions()
						.position( latLng )
						.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE ) )
						.title( "I am here" ) );
			}
			
			
			mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 22 ) );
			
			
		}
		
		@Override
		public void onStatusChanged( String provider, int status, Bundle extras )
		{
			
		}
		
		@Override
		public void onProviderEnabled( String provider )
		{
			
		}
		
		@Override
		public void onProviderDisabled( String provider )
		{
			
		}
	};
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		// AIzaSyAS8VQixt6IlrViHNqkbYmxxdMM-PXjUZQ
		
		mMapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
		mMapFrag.getMapAsync( this );
		
		
		locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		
		
	}
	
	@SuppressWarnings("MissingPermission")
	@Override
	public void onMapReady( GoogleMap googleMap )
	{
		
		
		mMap = googleMap;
		
		
		try
		{
			kmlLayer = new KmlLayer( mMap, R.raw.cis_shp, getApplicationContext() );
			kmlLayer.addLayerToMap();
			
			
		}
		catch ( IOException e )
		{
			Toast.makeText( getApplicationContext(), "IO Excep", Toast.LENGTH_SHORT ).show();
			e.printStackTrace();
		}
		catch ( XmlPullParserException e )
		{
			Toast.makeText( getApplicationContext(), "XMl Parser Excep", Toast.LENGTH_SHORT ).show();
			e.printStackTrace();
		}
		
		
		if ( checkPermissions() )
		{
			
			Criteria criteria = new Criteria();
			
			criteria.setAccuracy( Criteria.ACCURACY_FINE );
			
			locationManager.requestLocationUpdates( (long) 1000, 0f, criteria, locationListener, null );
			
			
			//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			
		}
		else
		{
			
			
			requestPermissions();
		}
		
		
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		
	}
	
	/**
	 * Callback received when a permissions request has been completed.
	 */
	@SuppressWarnings("MissingPermission")
	@Override
	public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults )
	{
		Log.i( "LOC", "onRequestPermissionResult" );
		if ( requestCode == 224 )
		{
			if ( grantResults.length <= 0 )
			{
				// If user interaction was interrupted, the permission request is cancelled and you
				// receive empty arrays.
				Log.i( "LOC", "User interaction was cancelled." );
			}
			else if ( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED )
			{
				Criteria criteria = new Criteria();
				criteria.setAccuracy( Criteria.ACCURACY_FINE );
				
				locationManager.requestLocationUpdates( (long) 1000, 0f, criteria, locationListener, null );
				//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			}
			else
			{
				// Permission denied.
				
				
				Toast.makeText( this, "Allow app to use location.", Toast.LENGTH_SHORT ).show();
			}
		}
	}
	
	private void requestPermissions()
	{
		boolean shouldProvideRationale =
				ActivityCompat.shouldShowRequestPermissionRationale( this,
						android.Manifest.permission.ACCESS_FINE_LOCATION );
		
		// Provide an additional rationale to the user. This would happen if the user denied the
		// request previously, but didn't check the "Don't ask again" checkbox.
		if ( shouldProvideRationale )
		{
			Log.i( "LOC", "Displaying permission rationale to provide additional context." );
			Toast.makeText( this, "Allow app to use location.", Toast.LENGTH_SHORT ).show();
		}
		else
		{
			Log.i( "LOC", "Requesting permission" );
			// Request permission. It's possible this can be auto answered if device policy
			// sets the permission in a given state or the user denied the permission
			// previously and checked "Never ask again".
			ActivityCompat.requestPermissions( MainActivity.this,
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					224 );
		}
	}
	
	private boolean checkPermissions()
	{
		int permissionState = ActivityCompat.checkSelfPermission( this,
				android.Manifest.permission.ACCESS_FINE_LOCATION );
		return permissionState == PackageManager.PERMISSION_GRANTED;
	}
	
}
