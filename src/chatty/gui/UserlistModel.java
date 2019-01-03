
package chatty.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.swing.AbstractListModel;

/**
 * The data model behind the userlist, sorts items.
 *
 * @author tduva
 */
public class UserlistModel<T extends Comparable> extends AbstractListModel {

    ArrayList<T> data = new ArrayList<>();
    ArrayList<T> tmpData = new ArrayList<>();
    private boolean isTmp = false;

    public ArrayList<T> getData() {
        return (ArrayList) data.clone();
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public T getElementAt(int index) {
        return data.get(index);
    }

    public void add(T item) {
        if (!isTmp) {
            System.out.println("Added to data " + item.toString());
            int insertionPoint = findInsertionPoint(item);
            data.add(insertionPoint, item);
            super.fireIntervalAdded(this, insertionPoint, insertionPoint);
        } else {
            System.out.println("Added to tmpData " + item.toString());
            int insertionPoint = findInsertionPoint(item);
            tmpData.add(insertionPoint, item);
            super.fireIntervalAdded(this, insertionPoint, insertionPoint);
        }
    }

    public void remove(T item) {
        int index;
        if (!isTmp)
            index = data.indexOf(item);
        else
            index = tmpData.indexOf(item);
        if (index == -1) {
            return;
        }
        if (!isTmp)
            data.remove(index);
        else
            tmpData.remove(index);
        super.fireIntervalRemoved(this, index, index);
    }

    private int findInsertionPoint(T item) {
        if (!isTmp) {


            int insertionPoint = Collections.binarySearch(data, item, null);
            if (insertionPoint < 0) {
                insertionPoint = -(insertionPoint + 1);
            }
            return insertionPoint;
        } else {
            int insertionPoint = Collections.binarySearch(tmpData, item, null);
            if (insertionPoint < 0) {
                insertionPoint = -(insertionPoint + 1);
            }
            return insertionPoint;
        }

    }

    public void updated(T item) {
        int index = data.indexOf(item);
        if (index == -1) {
            return;
        }
        super.fireContentsChanged(this, index, index);
    }

    public void clear() {
        if (!data.isEmpty()) {
            super.fireIntervalRemoved(this, 0, data.size() - 1);
            data.clear();
        }
    }

    /**
     * Manually sort entries. This may sometimes fix the sorting.
     */
    public void sort() {
        Collections.sort(data);
        super.fireContentsChanged(this, 0, data.size() - 1);
    }

    /**
     * Mark all entries as changed, so they get repainted.
     */
    public void update() {
        super.fireContentsChanged(this, 0, data.size() - 1);
    }

    public void setFilteredData(String param) {
        if (!isTmp) {
            tmpData = (ArrayList) data.clone();
            data = (ArrayList) data.stream().filter(e -> e.toString().toLowerCase().contains(param.toLowerCase())).collect(Collectors.toList());
            isTmp = true;
        } else {
            data = (ArrayList) data.stream().filter(e -> e.toString().toLowerCase().contains(param.toLowerCase())).collect(Collectors.toList());
        }

    }

    public void unfilterData() {
        if (isTmp) {
            data = (ArrayList) tmpData.clone();
            tmpData.clear();
            isTmp = false;
        }
    }
}
