
package com.castro.epoc;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.widget.EditText;

public class Profiles {

    public Profiles() {
    }

    public CharSequence[] mUsers;
    private static String sActiveUser = null;
    private static AlertDialog sDialog;
    private static File sUserFile = new File(Environment.getExternalStorageDirectory()
            + "/EPOC/users.xml");

    /** Getters and setters. */
    public static void setActiveUser(String s) {
        sActiveUser = s;
    }

    public static String getActiveUser() {
        return sActiveUser;
    }

    public static File getUserFile() {
        return sUserFile;
    }

    /**
     * Gets all saved user profiles.
     * 
     * @param f The file where users are stored
     * @return A {@link CharSequence}[] with all stored users
     */
    private CharSequence[] getAllUsers(File f) {
        CharSequence[] allUsers = null;
        // Initiates and normalizes the file to be read.
        Document doc = Files.getDoc(f);
        doc.getDocumentElement().normalize();
        // Gets all users in a NodeList.
        NodeList users = doc.getElementsByTagName("user");
        int totalUsers = users.getLength();
        allUsers = new CharSequence[totalUsers];
        for (int x = 0; x < totalUsers; x++) {
            String oneUser = users.item(x).getTextContent();
            allUsers[x] = oneUser;
        }
        return allUsers;
    }

    /**
     * Checks if the users file exists before returning the users list. If it
     * doesn't exist, creates a new one and attempts to create a new user.
     * 
     * @param f The specified file
     * @param c Context to display the profile creation dialog
     */
    private void generateUsersList(File f) {
        if (!f.exists()) {
            Files.createFile(f, "users");
            return;
        }
        mUsers = getAllUsers(f);
    }

    /**
     * Prompts a profile selection dialog.
     * 
     * @param c Context to display the dialog
     */
    public AlertDialog userSelection(final Context c) {
        if (sDialog != null && sDialog.isShowing())
            sDialog.cancel();
        generateUsersList(sUserFile);
        if (mUsers == null)
            newUserDialog(c).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Please select your profile or create a new one");
        builder.setNegativeButton("Cancel", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNeutralButton("New", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                newUserDialog(c).show();
            }
        });
        builder.setItems(mUsers, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                sActiveUser = mUsers[which].toString();
                ActionBarManager.setUser(mUsers[which].toString());
                // TODO: UPDATE MACHINE LEARNING HERE
            }
        });
        sDialog = builder.create();
        return sDialog;
    }

    /**
     * Dialog to create a new profile.
     * 
     * @param c Context to display the dialog
     */
    private AlertDialog newUserDialog(Context c) {
        AlertDialog.Builder newUser = new AlertDialog.Builder(c);
        newUser.setMessage("Please enter your name:");
        final EditText input = new EditText(c);
        newUser.setView(input);
        newUser.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = input.getText().toString();
                addUser(s);
                sActiveUser = s;
                ActionBarManager.setUser(s);
                // TODO: UPDATE MACHINE LEARNING HERE
            }
        });
        newUser.setNegativeButton("Cancel", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = newUser.create();
        return dialog;
    }

    /**
     * Creates a new profile and saves it on the users file.
     * 
     * @param s The user's name
     */
    private void addUser(String s) {
        if (!sUserFile.exists()) {
            Files.createFile(sUserFile, "users");
        }
        Document doc = Files.getDoc(sUserFile);
        Node root = doc.getFirstChild();
        Element user = doc.createElement("user");
        root.appendChild(user);
        user.setTextContent(s);
        Files.saveChanges(doc, sUserFile);
    }
}