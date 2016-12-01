package il.co.yomanim.mobileyomanim.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import core.helpers.ConstantsHelper;
import core.model.settings.Language;
import core.utils.LocaleUtils;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.activities.MainActivity;
import il.co.yomanim.mobileyomanim.adapters.LanguageAdapter;

public class LanguageFragment extends Fragment {

    public LanguageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_language, container, false);

        //hide toolbar filter buttons
        ((MainActivity)getActivity()).hideToolbarFilters();

        Language english = new Language(
                getResources().getString(R.string.language_english),
                ConstantsHelper.LOCALIZATION_ENGLISH,
                R.mipmap.ico_flag_en);
        Language hebrew = new Language(
                getResources().getString(R.string.language_hebrew),
                ConstantsHelper.LOCALIZATION_HEBREW,
                R.mipmap.ico_flag_il);
        Language arabic = new Language(
                getResources().getString(R.string.language_arabic),
                ConstantsHelper.LOCALIZATION_ARABIC,
                R.mipmap.ico_flag_ar);
        Language russian = new Language(
                getResources().getString(R.string.language_russian),
                ConstantsHelper.LOCALIZATION_RUSSIAN,
                R.mipmap.ico_flag_ru);


        List<Language> list = new ArrayList<>();
        list.add(english);
        list.add(hebrew);
        list.add(arabic);
        list.add(russian);


        // Set adapter and populate listView
        final LanguageAdapter adapter = new LanguageAdapter(
                layout.getContext(),
                R.layout.item_language_row,
                list);
        final ListView lv = (ListView) layout.findViewById(R.id.listViewLanguages);
        lv.setAdapter(adapter);

        // listView onclick event
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Language item = (Language) parent.getAdapter().getItem(position);
                boolean result;

                if (item.getCultureCode().equalsIgnoreCase(""))
                    result = false;
                else {
                    try {
                        Locale myLocale = new Locale(item.getCultureCode());
                        LocaleUtils.saveLocale(item.getCultureCode(), layout.getContext());
                        Locale.setDefault(myLocale);
                        android.content.res.Configuration config = new android.content.res.Configuration();
                        config.locale = myLocale;
                        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
                        result = true;
                    } catch (Exception ex) {
                        Log.e("LocaleUtils", ex.getMessage());
                        result = false;
                    }
                }



//                if (LocaleUtils.changeLang(item.getCultureCode(), layout.getContext())) {
                if (result) {
                    //uncheck other
                    for (int i = 0; i < lv.getCount(); i++) {
                        View v = lv.getChildAt(i);
                        RadioButton radioItem = (RadioButton) v.findViewById(R.id.radioButtonLang);
                        radioItem.setChecked(false);
                    }

                    //check radio
                    RadioButton radio = (RadioButton)view.findViewById(R.id.radioButtonLang);
                    radio.setChecked(true);

                    //show message
                    Toast toast = Toast.makeText(layout.getContext(),
                            MessageFormat.format("Changed locale to {0}", item.getName()),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();

                    //TODO: can't reload current fragment, so just load Timetable
//                    ((MainActivity) getActivity()).ReloadFragmentsWithLocale(3);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                }
                else {
                    //Toast
                    Toast toast = Toast.makeText(layout.getContext(),
                            MessageFormat.format("Error changing locale to {0}", item.getName()),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            }
        });

        return layout;
    }
}