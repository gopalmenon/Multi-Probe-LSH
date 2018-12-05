package images;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

public class SerializableHistogram implements Serializable {

    public List<Double> features;
    public String id;
    public String url;

    public SerializableHistogram(List<Double> features, String id, String url){
        this.features = features;
        this.id = id;
        this.url = url;
    }
}
