
package com.castro.epoc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import static com.castro.epoc.Global.*;

public class Help extends DialogFragment {

    private String mTitle;

    private String mMessage;

    private String mKeyboardMessage = "\nThis section is used to type simple messages.\n\n"
            + "By winking your right eye you can navigate through the alphabet. "
            + "Before any selections, right winks will move through the rows, after the "
            + "first selection it will change the active column.\n\n"
            + "Winking your left eye will firstly select the active row and secondly "
            + "type the selected character. If nothing is selected, left winks will "
            + "delete the last typed character.\n";

    private String mContactsMessage = "\nThe contacts section is used to select a contact using only your head movements.\n\n"
            + "Moving your head sideways will change the active selection. Nodding will "
            + "start calling the selected contact. Nodding again cancels the selection.\n";

    private String mCalculationMessage = "\nCalculation is a mini-game where you have to select the right operation to get the "
            + "result on the center.\n\n"
            + "To make a selection, you have to look completely to the side of the "
            + "correct answer. The result will change color according to your answer.\n\n"
            + "Each round has 4 seconds. To pause the game, press the result text. "
            + "The difficulty is set by pressing the Level text, and will automatically "
            + "increase after 10 tries.\n";

    private String mOscillationMessage = "\nThis section shows you the oscillation levels of your "
            + "brain signals sorted by frequency.\n\n" + "Delta waves: up to 4 Hz\n"
            + "Theta waves: 4 to 8 Hz\n" + "Alpha waves: 8 to 13 Hz\n"
            + "Beta waves: 13 to 30 Hz\n";

    private String mConfigurationMessage = "\nThe configuration tool is used both to visualize your "
            + "EEG activity and to train specific actions. You can also check the sensors "
            + "quality, which is color coded, and select which sensors to display.\n\n"
            + "You can visualize your real-time EEG activity or load pre-recorded data.\n\n"
            + "To train an action, select it on the bottom right list, press train and "
            + "then perform the specified action. When you're done, press train again.\n"
            + "To clear the data for that action by pressing Clear, or delete all "
            + "trained values by holding the same button for a few seconds.\n";

    public Help() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle).setMessage(mMessage)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return builder.create();
    }

    public void setCurrent(int current) {
        switch (current) {
            case KEY_PAGE:
                setHelp(mKeyboardMessage);
                setTitle("Help - Keyboard");
                return;
            case CONT_PAGE:
                setHelp(mContactsMessage);
                setTitle("Help - Contacts");
                return;
            case CALC_PAGE:
                setHelp(mCalculationMessage);
                setTitle("Help - Calculation");
                return;
            case CONC_PAGE:
                setHelp(mOscillationMessage);
                setTitle("Help - Oscillation");
                return;
            case CONFIG_PAGE:
                setHelp(mConfigurationMessage);
                setTitle("Help - Configuration");
                return;
            default:
                return;
        }
    }

    private void setHelp(String s) {
        mMessage = s;
    }

    private void setTitle(String s) {
        mTitle = s;
    }
}
