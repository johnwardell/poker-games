package johnwardell.games.texasholdempoker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayersFragment extends Fragment {
	
	private TextView[] mNames = new TextView[9];
	private TextView[] mChips = new TextView[9];
	private ImageView[] mIndicators = new ImageView[9];
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.players_fragment, container, false);
		
		mNames[0] = (TextView) v.findViewById(R.id.name_one);
		mNames[1] = (TextView) v.findViewById(R.id.name_two);
		mNames[2] = (TextView) v.findViewById(R.id.name_three);
		mNames[3] = (TextView) v.findViewById(R.id.name_four);
		mNames[4] = (TextView) v.findViewById(R.id.name_five);
		mNames[5] = (TextView) v.findViewById(R.id.name_six);
		mNames[6] = (TextView) v.findViewById(R.id.name_seven);
		mNames[7] = (TextView) v.findViewById(R.id.name_eight);
		mNames[8] = (TextView) v.findViewById(R.id.name_nine);
		
		mChips[0] = (TextView) v.findViewById(R.id.chips_one);
		mChips[1] = (TextView) v.findViewById(R.id.chips_two);
		mChips[2] = (TextView) v.findViewById(R.id.chips_three);
		mChips[3] = (TextView) v.findViewById(R.id.chips_four);
		mChips[4] = (TextView) v.findViewById(R.id.chips_five);
		mChips[5] = (TextView) v.findViewById(R.id.chips_six);
		mChips[6] = (TextView) v.findViewById(R.id.chips_seven);
		mChips[7] = (TextView) v.findViewById(R.id.chips_eight);
		mChips[8] = (TextView) v.findViewById(R.id.chips_nine);
		
		mIndicators[0] = (ImageView) v.findViewById(R.id.light_one);
		mIndicators[1] = (ImageView) v.findViewById(R.id.light_two);
		mIndicators[2] = (ImageView) v.findViewById(R.id.light_three);
		mIndicators[3] = (ImageView) v.findViewById(R.id.light_four);
		mIndicators[4] = (ImageView) v.findViewById(R.id.light_five);
		mIndicators[5] = (ImageView) v.findViewById(R.id.light_six);
		mIndicators[6] = (ImageView) v.findViewById(R.id.light_seven);
		mIndicators[7] = (ImageView) v.findViewById(R.id.light_eight);
		mIndicators[8] = (ImageView) v.findViewById(R.id.light_nine);
		
		return v;
	}
	
	public void setName(String name, int index) {
		mNames[index].setText(name);
	}
	
	public void setChips(String chips, int index) {
		mChips[index].setText(chips);
	}
	
	public void setIndicatorImage(int lightImage, int index) {
		mIndicators[index].setImageResource(lightImage);
	}
	
	public void switchToBetFragment(View v) {
		
	}

}
