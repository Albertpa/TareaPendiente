package com.aprendeandroid.tareapendiente;

import java.util.Calendar;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.support.v4.app.DialogFragment;
import android.text.format.Time;
import android.util.Log;
import android.widget.TimePicker;



public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	
	private TimePickerListener listener;
	private long tiempoGuardado = -1; //Aqui se guarda lo que viene en el Bundle
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//Verify that the host activity implements the callback interface
		try {
			//Instantiate the DatePickerListener so we can send events to the host
			listener = (TimePickerListener) activity; //esta linea es obligatoria
		} catch (Exception e) {
			//The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement TimePickerListener");	
		}
	}




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Recogemos los parametros del bundle
		
		Bundle parametros = getArguments(); //Aqui recojemos el Bundle
		
		if(parametros != null){//Si no viene vacio lo leemos			
			tiempoGuardado = parametros.getLong("tiempoGuardado", -1);
			
		}
		
	}

	
	
	//Aqui tenemos la seguridad de que esta todo creado, es el último paso del ciclo
	//de vida y aqui podemos modificar el DatePickerDialog
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		Dialog dialog = getDialog();//Cojemos el dialogo como clase base Dialog
		
		TimePickerDialog timeDialog = (TimePickerDialog) dialog; //Aqui le hacemos casting a TimePickerDialog
		
		int HH, mm;
		
		long msTiempo = tiempoGuardado; //le ponemos la fecha del bundle, es -1 si no es establecido
		
		if(msTiempo > -1){ //le ponemos la que viene en el Bundle (tiempo)
			Time timeOfJob = new Time();		
			timeOfJob.set(msTiempo);
			
			HH = timeOfJob.hour;
			mm = timeOfJob.minute;
			
			
		}else{
			//sino le ponemos la actual
			final Calendar c = Calendar.getInstance();		
			HH = c.get(Calendar.HOUR_OF_DAY);
			mm = c.get(Calendar.MINUTE);
			
		}
		timeDialog.updateTime(HH, mm); //Actualizamos el TimePickerDialog con lo que haya ocurrido
	}




	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		//Creamos el dialogo con una hora iniciar (la actual)	
		final Calendar c = Calendar.getInstance();		
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		boolean is24HourView = true;
		
		
		//Dialogo implicito, incluido en sdk		
		return new TimePickerDialog(getActivity(), this, hourOfDay, minute, is24HourView);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		final Calendar c = Calendar.getInstance();	
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		/*
		c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        //instead of c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        */
		Time timeOfJob = new Time();		
		
		timeOfJob.set(00, minute, hourOfDay, day, month, year);
		long toj = timeOfJob.toMillis(true);
		
		//devolvemos el dato atras
		listener.tiempoEstablecido(toj);
	}
	
	
	//Interface
	public static interface TimePickerListener{
		
		public void tiempoEstablecido(long milis);
	}

	

}
