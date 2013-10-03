/*
 * project iShareVegas
 * 
 * package com.CarmenJohnson_Wk2.isharevegas
 * 
 * @author Carmen Johnson
 * 
 * date Sep 12, 2013
 * 
 */
package com.CarmenJohnson_Wk2.isharevegas;

import org.json.JSONObject;

import android.os.Bundle;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.CarmenJohnson.library.Form;
import com.CarmenJohnson.library.ValueLabelPair;
import com.mynet.NetConnector;

public class MainActivity extends Activity {
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout layout = new LinearLayout(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(R.layout.activity_main);
		
		
		
		
		 
		 final Spinner spinnerFrom = (Spinner)findViewById(R.id.SpinnerFromCurrency);
		 Form.fillSpinnerWithCurrency(MainActivity.this, spinnerFrom);
		 final Spinner spinnerTo = (Spinner)findViewById(R.id.SpinnerToCurrency);
		 Form.fillSpinnerWithCurrency(MainActivity.this, spinnerTo);
		 final TextView textViewResultPart1 = (TextView)findViewById(R.id.textViewResultPart1);
		 final TextView textViewResultPart2 = (TextView)findViewById(R.id.textViewResultPart2);
		 final TextView textViewResultPart3 = (TextView)findViewById(R.id.textViewResultPart3);
		 final TextView textViewMessage = (TextView)findViewById(R.id.TextViewMessage);
		 Button convertButton = (Button)findViewById(R.id.buttonConvert);
		 convertButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Get Data
				final SharedPreferences sp = getSharedPreferences("app", MODE_PRIVATE);
				try{
					
					final String currencyFrom = ((ValueLabelPair)spinnerFrom.getSelectedItem()).getValue();
					
					final String currencyTo = ((ValueLabelPair)spinnerTo.getSelectedItem()).getValue();
					
					if(!NetConnector.isNetworkConnected(MainActivity.this))
					{
						String message = "Network connection error!.";
						if(sp.contains(currencyFrom+"-"+currencyTo))
						{
							message += "\n Sshowing lastly fetched data:\n";
							String parts[] = sp.getString(currencyFrom+"-"+currencyTo, "").split("=");
							textViewResultPart1.setText(parts[0]);
							textViewResultPart2.setText("=");
							textViewResultPart3.setText(parts[1]);
						}
						textViewMessage.setText(message);
					}
					
					
					final String url = "http://www.google.com/ig/calculator?hl=en&q=1"+currencyFrom+"=?"+currencyTo;
					
					Thread thread = new Thread(new Runnable(){
					    @Override
					    public void run() {
					        try {
					        	final JSONObject jo = NetConnector.getJSON(MainActivity.this, url);

					        	runOnUiThread(new Runnable(){

									@Override
									public void run() {
										try
										{
											if(jo==null)
											{
												textViewMessage.setText("Some exception has occured");
											}
											else if(jo.has("error") && !jo.getString("error").equals("0") && !jo.getString("error").isEmpty())
											{
												textViewMessage.setText(jo.getString("error"));
											}
											else
											{
												textViewMessage.setText("");
												Editor ed = sp.edit();
												ed.putString(currencyFrom+"-"+currencyTo, jo.getString("lhs")+" = "+ jo.getString("rhs"));
												ed.commit();
												textViewResultPart1.setText(jo.getString("lhs"));
												textViewResultPart2.setText("=");
												textViewResultPart3.setText(jo.getString("rhs"));
											}
										}
										catch(Exception ex)
										{
											
										}
									}
					        			
					        	});
								
					        } catch (Exception e) {
					            e.printStackTrace();
					        }
					    }
					});

					thread.start(); 
					
					
					
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
