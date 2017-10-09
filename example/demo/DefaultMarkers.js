import React from 'react';
import {
    StyleSheet,
    View,
    Text,
    Dimensions,
    TouchableOpacity,
} from 'react-native';

import AMapView from 'react-native-map-amap';

const {width, height} = Dimensions.get('window');

const ASPECT_RATIO = width / height;
const LATITUDE = 31.238068;
const LONGITUDE = 121.501654;
const LATITUDE_DELTA = 0.0922;
const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;
let id = 0;

function randomColor() {
    return `#${Math.floor(Math.random() * 16777215).toString(16)}`;
}

export default class DefaultMarkers extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            region: {
                latitude: LATITUDE,
                longitude: LONGITUDE,
                latitudeDelta: LATITUDE_DELTA,
                longitudeDelta: LONGITUDE_DELTA,
            },
            markers: [],
        };
    }

    onMapPress(e) {
        alert(1);
        this.setState({
            markers: [
                ...this.state.markers,
                {
                    coordinate: e.nativeEvent.coordinate,
                    key: id++,
                    color: randomColor(),
                },
            ],
        });
    }

    render() {
        return (
            <View style={styles.container}>
                <AMapView
                    provider={'default'}
                    style={styles.map}
                    initialRegion={this.state.region}
                    onPress={(e) => this.onMapPress(e)}
                >
                    {this.state.markers.map(marker => (
                        <AMapView.Marker
                            key={marker.key}
                            coordinate={marker.coordinate}
                            pinColor={marker.color}
                        />
                    ))}
                </AMapView>
                <View style={styles.buttonContainer}>
                    <TouchableOpacity
                        onPress={() => this.setState({markers: []})}
                        style={styles.bubble}
                    >
                        <Text>Tap to create a marker of random color</Text>
                    </TouchableOpacity>
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        ...StyleSheet.absoluteFillObject,
        justifyContent: 'flex-end',
        alignItems: 'center',
    },
    map: {
        ...StyleSheet.absoluteFillObject,
    },
    bubble: {
        backgroundColor: 'rgba(255,255,255,0.7)',
        paddingHorizontal: 18,
        paddingVertical: 12,
        borderRadius: 20,
    },
    latlng: {
        width: 200,
        alignItems: 'stretch',
    },
    button: {
        width: 80,
        paddingHorizontal: 12,
        alignItems: 'center',
        marginHorizontal: 10,
    },
    buttonContainer: {
        flexDirection: 'row',
        marginVertical: 20,
        backgroundColor: 'transparent',
    },
});