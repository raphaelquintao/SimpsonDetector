package br.raphael.extended;

import java.util.ArrayList;
import java.util.List;

import br.raphael.detector.ObjectList;
import br.raphael.detector.R;
import br.raphael.detector.R.id;
import br.raphael.detector.R.layout;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterTwoLinesImage extends BaseAdapter{
	private static final String TAG = null;
	public ArrayList<ObjectList> personal;
	Context c;
	
	public AdapterTwoLinesImage(Context context, ArrayList<ObjectList> data){
		c = context;
		personal = data;
		
	}

	@Override
	public int getCount() {
		return personal.size();
	}
	

	@Override
	public Object getItem(int arg0) {
		return personal.get(arg0);
	}


	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int index, View v, ViewGroup parent) {
		View view = v;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.adapter_img_two_lines, null);
		}
		
		ObjectList linha = personal.get(index);
		ImageView i = (ImageView) view.findViewById(R.id.adapter_icon);
		TextView t = (TextView) view.findViewById(R.id.adapter_title);
		TextView m = (TextView) view.findViewById(R.id.adapter_message);
		
		i.setImageResource(linha.getIcon());
		t.setText(linha.getTitle());
		m.setText(linha.getMsg());


		return view;
	}

   
}


