package il.co.yomanim.mobileyomanim.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.List;
import core.model.settings.Language;
import core.utils.LocaleUtils;
import il.co.yomanim.mobileyomanim.R;


public class LanguageAdapter extends ArrayAdapter<Language> {
    private Context context;
    private List<Language> languagesList;

    public LanguageAdapter(Context context, int resource, List<Language> objects) {
        super(context, resource, objects);
        this.context = context;
        this.languagesList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_language_row, parent, false);

        Typeface fontRobotoMedium = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");

        Language language = languagesList.get(position);

        String localeCode = LocaleUtils.getCurrentLocale(context);

        //image
        ImageView flag = (ImageView)view.findViewById(R.id.imageViewLangFlag);
        flag.setImageResource(language.getResource());

        //name
        TextView tv = (TextView) view.findViewById(R.id.textViewLangTitle);
        tv.setTypeface(fontRobotoMedium);
        tv.setText(language.getName());

        //radio
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.radioButtonLang);
        if (localeCode.equals(language.getCultureCode())) {
            radioButton = (RadioButton) view.findViewById(R.id.radioButtonLang);
            radioButton.setChecked(true);
        }

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setChecked(false);
                    // Code to display your message.
                }
            }
        });



        return view;
    }
}