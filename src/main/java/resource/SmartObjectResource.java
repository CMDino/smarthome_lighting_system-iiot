package resource;

import java.util.ArrayList;
import java.util.List;

public abstract class SmartObjectResource<T> {

    private String id;

    private String type;

    protected List<ResourceDataListener<T>> resourceDataListenerList;

    public SmartObjectResource() {
        this.resourceDataListenerList = new ArrayList<>();
    }

    public SmartObjectResource(String id, String type) {
        this.id = id;
        this.type = type;
        this.resourceDataListenerList = new ArrayList<>();
    }

    public abstract T loadUpdateValue();

    protected void notifyUpdate(T updatedValue){
        if(this.resourceDataListenerList != null && this.resourceDataListenerList.size() > 0)
            this.resourceDataListenerList.forEach(resourceDataListener -> {
                if(resourceDataListener != null)
                    resourceDataListener.onDataChanged(this, updatedValue);
            });
    }

    public void addDataListener(ResourceDataListener<T> resourceDataListener){
        if(this.resourceDataListenerList != null){
            this.resourceDataListenerList.add(resourceDataListener);
        }
    }

    public void removeDataListener(ResourceDataListener<T> resourceDataListener){
        if(this.resourceDataListenerList != null && this.resourceDataListenerList.contains(resourceDataListener)){
            this.resourceDataListenerList.remove(resourceDataListener);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SmartObjectResource{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
