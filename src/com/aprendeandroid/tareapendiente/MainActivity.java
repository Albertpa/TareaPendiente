package com.aprendeandroid.tareapendiente;

import java.text.SimpleDateFormat;

import com.aprendeandroid.tareapendiente.TimePickerFragment.TimePickerListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements TimePickerListener{
	/*
	 * Albert Pagès Raventos
	 * */
	private String tarea = null;
	private long timeOfJob = 0l;
	private int prioridad = 0;
	
	
	
	private SharedPreferences mTareaSettings;
	
	
	//-----------CICLO DE VIDA ACTIVITY--------------//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//manager para la generacion de las preferencias
		mTareaSettings = getSharedPreferences(Constants.TAREA_PREFERENCES, Context.MODE_PRIVATE);
	
		//Inicializadores
		initTarea();
		initTimePicker();
		initPrioritySpinner();
	
	}
	
	
	@Override
	protected void onPause() {
		//Cuando se pulse back, si llaman, etc...
		guardarPreferences();
		super.onPause();
	}

	//-----------INICIALIZADORES DE WIDGETS SEGUN DATOS DE LAS PREFERENCIAS--------------//

	private void initTarea(){
		EditText campoTarea = (EditText) findViewById(R.id.campoTextoTarea);
		if(mTareaSettings.contains(Constants.NOMBRE_TAREA)){
			tarea = mTareaSettings.getString(Constants.NOMBRE_TAREA, "");
			campoTarea.setText(tarea);
		
		}
	}
	
	private void initTimePicker(){
		TextView campoTiempo = (TextView) findViewById(R.id.campoTimeTarea);
		
		if(mTareaSettings.contains(Constants.TOJ)){
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
			campoTiempo.setText(simpleDateFormat.format(mTareaSettings.getLong(Constants.TOJ, 0)));
					
			timeOfJob = mTareaSettings.getLong(Constants.TOJ, 0);	
		
		}else{
			campoTiempo.setText(R.string.seleccionTime);		
		}
		
	}

	
	private void initPrioritySpinner(){
		final Spinner spinner = (Spinner) findViewById(R.id.prioridadSpinner);
		
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.prioridades, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		if(mTareaSettings.contains(Constants.PRIORIDAD)){
			prioridad = mTareaSettings.getInt(Constants.PRIORIDAD, 0);
			spinner.setSelection(prioridad);
		}	
		//Handle spinner selections
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View itemSelected,int selectedItemPosition, long selectedId) {
				prioridad = selectedItemPosition;					
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent
					) {}
				
		});
		
		
	}

	//-----------LANZADORES DE DIALOGOS DESDE LA VISTA--------------//
	public void onPickTimeButtonClick(View v){
		DialogFragment newFragment = new TimePickerFragment();
		
		//Le pasamos el tiempo guardado si es que hay
		long milis = -1;
		if(mTareaSettings.contains(Constants.TOJ)){
			milis = mTareaSettings.getLong(Constants.TOJ, 0);
		}
		Bundle parametros = new Bundle();
		parametros.putLong("tiempoGuardado", milis);
		//fin de envio de datos al TimePickerFragment
		
		
		newFragment.setArguments(parametros);
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}



	
	
	//-----------INTERFACES PARA LOS DIALOGOS--------------//
	@Override
	public void tiempoEstablecido(long milis) {
		final TextView toj = (TextView) findViewById(R.id.campoTimeTarea);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		toj.setText(simpleDateFormat.format(milis));
	
		
		timeOfJob = milis;
		
		
	}
	
	//-----------LLAMADAS A METODOS DESDE LA VISTA-------------//
	
	public void accionClick(View v) {
			
		switch(v.getId()){
			case R.id.aAceptar:
				guardarPreferences();
			break;
			case R.id.aBorrar:
				borrarPreferences();
			break;
		}
	}
			
	
	//-----------GESTION DE PREFERENCIAS--------------//
	
	private void guardarPreferences() {
		
		//se capturan aqui los datos, solo es leer los editText, no tienen
		//metodos propios en esta clase
		EditText descripcionTarea = (EditText) findViewById(R.id.campoTextoTarea);
		
		tarea = descripcionTarea.getText().toString();
		
		//Gestor
		Editor editor = mTareaSettings.edit();
		//si se ha editado el texto guardaremos su dato
		if(!tarea.equals("")){
			editor.putString(Constants.NOMBRE_TAREA, tarea);
		}
		
		//si se ha editado el tiempo entraremos aqui:
		if(timeOfJob != 0){
			editor.putLong(Constants.TOJ, timeOfJob);
		}
		//En el caso de la prioridad, como mínimo siempre mostrara la posición 0, por lo que
		//podemos guardarlo siempre aunque no se haya modificado
		editor.putInt(Constants.PRIORIDAD, prioridad);
				
		editor.commit();
			
	}
	
	private void borrarPreferences() {
		
		//prioridad = 0;
		
		//Gestor
		//se borran aqui los datos, solo es leer del archivo y, si existe el campo, borrar el dato. 
		Editor editor = mTareaSettings.edit();
		
		if(mTareaSettings.contains(Constants.NOMBRE_TAREA)){
			editor.remove(Constants.NOMBRE_TAREA);
			//editor.putString(Constants.NOMBRE_TAREA, tarea);
		}	
		if(mTareaSettings.contains(Constants.TOJ)){
			editor.remove(Constants.TOJ);
			//editor.putLong(Constants.TOJ, timeOfJob);
		}	
		if(mTareaSettings.contains(Constants.PRIORIDAD)){
			editor.remove(Constants.PRIORIDAD);
			//editor.putInt(Constants.PRIORIDAD, prioridad);
		}	
		editor.commit();
		
		//Borramos la información en pantalla
		
		//Texto
		EditText campoTarea = (EditText) findViewById(R.id.campoTextoTarea);
		tarea = "";
		campoTarea.setText(tarea);
				
				
		//Tiempo
		TextView campoTiempo = (TextView) findViewById(R.id.campoTimeTarea);
		timeOfJob = 0l;
		campoTiempo.setText(R.string.seleccionTime);		
	
		//Prioridad
		prioridad=0;
		//Inicia el spinner, como no hay ningún dato modificado guardado lo inicia de 0
		initPrioritySpinner();
		
			
	}
	
	

}
