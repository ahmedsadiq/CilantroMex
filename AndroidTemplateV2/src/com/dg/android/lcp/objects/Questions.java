/**
 * 
 */
package com.dg.android.lcp.objects;


/**
 * @author MehrozKarim
 *
 */
public class Questions {
    String id;
    String question_type;
    String text;
    QuestionChoices question_choices[];
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * @return the question_type
     */
    public String getQuestion_type() {
        return question_type;
    }
    
    /**
     * @param question_type the question_type to set
     */
    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }
    
    /**
     * @return the text
     */
    public String getText() {
        return text;
    }
    
    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * @return the question_choices
     */
    public QuestionChoices[] getQuestion_choices() {
        return question_choices;
    }
    
    /**
     * @param question_choices the question_choices to set
     */
    public void setQuestion_choices(QuestionChoices[] question_choices) {
        this.question_choices = question_choices;
    }
    
    
}
