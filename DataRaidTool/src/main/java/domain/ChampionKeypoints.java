package domain;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.feature.local.keypoints.Keypoint;

import java.io.Serializable;

public class ChampionKeypoints implements Serializable {
    LocalFeatureList<Keypoint> keypoints;

    public LocalFeatureList<Keypoint> getKeypoints() {
        return keypoints;
    }

    public void setKeypoints(LocalFeatureList<Keypoint> keypoints) {
        this.keypoints = keypoints;
    }
}
