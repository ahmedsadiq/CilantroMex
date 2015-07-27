/**
 * 
 */
package com.dg.android.lcp.objects;


/**
 * @author MehrozKarim
 * 
 */
public class Survey {

    String id;
    String title;
    Questions questions[];

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the questions
     */
    public Questions[] getQuestions() {
        return questions;
    }

    /**
     * @param questions
     *            the questions to set
     */
    public void setQuestions(Questions[] questions) {
        this.questions = questions;
    }


}
