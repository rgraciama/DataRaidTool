package model;

import domain.Champion;

import java.util.List;


/**
 * Helper class to wrap a list of persons. This is used for saving the
 * list of persons to XML.
 *
 * @author Marco Jakob
 */
public class PersonList {

    private List<Champion> champions;

    public List<Champion> getChampions() {
        return champions;
    }

    public void setChampions(List<Champion> champions) {
        this.champions = champions;
    }
}
