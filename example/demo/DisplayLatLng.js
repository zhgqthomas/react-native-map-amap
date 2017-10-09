import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    View,
    Dimensions,
    TouchableOpacity
} from 'react-native';
import AMapView from 'react-native-map-amap'

const {width, height} = Dimensions.get('window');

const ASPECT_RATIO = width / height;
const LATITUDE = 31.238068;
const LONGITUDE = 121.501654;
const LATITUDE_DELTA = 0.0922;
const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;

export default class DisplayLatLng extends Component {

    constructor(props) {
        super(props);

        this.state = {
            initialRegion: {
                latitude: LATITUDE,
                longitude: LONGITUDE,
                latitudeDelta: LATITUDE_DELTA,
                longitudeDelta: LONGITUDE_DELTA,
            },
            region: {
                latitude: LATITUDE,
                longitude: LONGITUDE,
                latitudeDelta: LATITUDE_DELTA,
                longitudeDelta: LONGITUDE_DELTA,
            },
        };

    }

    onRegionChange(region) {
        // This code gonna raise findNodeHandle inside render() warning.
        // This is just for demo
        this.setState({region});
    }

    jumpRandom = () => {
        this.setState({region: this.randomRegion()});
    };

    animateRandom = () => {
        this.map.animateToRegion(this.randomRegion());
    };

    animateRandomCoordinate = () => {
        this.map.animateToCoordinate(this.randomCoordinate());
    };

    randomCoordinate = () => {
        const region = this.state.region;
        return {
            latitude: region.latitude + ((Math.random() - 0.5) * (region.latitudeDelta / 2)),
            longitude: region.longitude + ((Math.random() - 0.5) * (region.longitudeDelta / 2)),
        }
    };

    randomRegion = () => {
        return {
            ...this.state.region,
            ...this.randomCoordinate(),
        }
    };

    render() {
        return (
            <View style={styles.container}>
                <AMapView
                    ref={ref => {
                        this.map = ref;
                    }}
                    provider={'default'}
                    style={styles.map}
                    region={this.state.region}
                    initialRegion={this.state.initialRegion}
                    onRegionChange={region => this.onRegionChange(region)}
                />
                <View style={[styles.bubble, styles.latlng]}>
                    <Text style={{textAlign: 'center'}}>
                        {this.state.region.latitude.toPrecision(7)},
                        {this.state.region.longitude.toPrecision(7)}
                    </Text>
                </View>
                <View style={styles.buttonContainer}>
                    <TouchableOpacity
                        onPress={() => this.jumpRandom()}
                        style={[styles.bubble, styles.button]}
                    >
                        <Text style={styles.buttonText}>Jump</Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                        onPress={() => this.animateRandom()}
                        style={[styles.bubble, styles.button]}
                    >
                        <Text style={styles.buttonText}>Animate (Region)</Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                        onPress={() => this.animateRandomCoordinate()}
                        style={[styles.bubble, styles.button]}
                    >
                        <Text style={styles.buttonText}>Animate (Coordinate)</Text>
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
        width: 100,
        paddingHorizontal: 8,
        alignItems: 'center',
        justifyContent: 'center',
        marginHorizontal: 5,
    },
    buttonContainer: {
        flexDirection: 'row',
        marginVertical: 20,
        backgroundColor: 'transparent',
    },
    buttonText: {
        textAlign: 'center',
    },
});