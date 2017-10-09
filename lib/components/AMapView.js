import React, {Component} from 'react'
import PropTypes from 'prop-types';
import {
    View,
    ViewPropTypes,
    requireNativeComponent,
    Platform,
    NativeModules,
    findNodeHandle,
} from 'react-native'
import MapMarker from './MapMarker'

const MAP_TYPES = {
    STANDARD: 'standard',
    SATELLITE: 'satellite',
    NIGHT: 'night',
    NAVI: 'navi',
    NONE: 'none'
};

const viewConfig = {
    uiViewClassName: 'MapView',
    validAttributes: {
        region: true,
        initialRegion: true,
        mapType: true,
    }
};

const viewPropTyeps = ViewPropTypes || View.propTypes;
const propTypes = {
    ...viewPropTyeps,
    style: viewPropTyeps.style,
    provider: PropTypes.oneOf([
        'texture', // use TextureMapView in AMap
        'default'
    ]).isRequired,
    mapType: PropTypes.oneOf(Object.values(MAP_TYPES)),
    showScaleControl: PropTypes.bool,
    showZoomControl: PropTypes.bool,
    showCompass: PropTypes.bool,
    showMyLocationButton: PropTypes.bool,
    showMapText: PropTypes.bool,
    showBuildings: PropTypes.bool,
    showTraffic: PropTypes.bool,
    scrollEnabled: PropTypes.bool,
    zoomEnabled: PropTypes.bool,
    rotateEnabled: PropTypes.bool,
    moveOnMarkerPress: PropTypes.bool,
    showsUserLocation: PropTypes.bool,
    /**
     * The region to be displayed by the map.
     *
     * The region is defined by the center coordinates and the span of
     * coordinates to display.
     */
    region: PropTypes.shape({
        /**
         * Coordinates for the center of the map.
         */
        latitude: PropTypes.number.isRequired,
        longitude: PropTypes.number.isRequired,

        /**
         * Difference between the minimun and the maximum latitude/longitude
         * to be displayed.
         */
        latitudeDelta: PropTypes.number.isRequired,
        longitudeDelta: PropTypes.number.isRequired,
    }),
    /**
     * The initial region to be displayed by the map.  Use this prop instead of `region`
     * only if you don't want to control the viewport of the map besides the initial region.
     *
     * Changing this prop after the component has mounted will not result in a region change.
     *
     * This is similar to the `initialValue` prop of a text input.
     */
    initialRegion: PropTypes.shape({
        /**
         * Coordinates for the center of the map.
         */
        latitude: PropTypes.number.isRequired,
        longitude: PropTypes.number.isRequired,

        /**
         * Difference between the minimun and the maximum latitude/longitude
         * to be displayed.
         */
        latitudeDelta: PropTypes.number.isRequired,
        longitudeDelta: PropTypes.number.isRequired,
    }),
    /**
     * Callback that is called once the map is fully loaded.
     */
    onMapLoaded: PropTypes.func,
    /**
     * Callback that is called when a marker on the map is tapped by the user.
     */
    onMarkerPress: PropTypes.func,
    /**
     * Callback that is called when user taps on the map.
     */
    onMapPress: PropTypes.func,
    /**
     * Callback that is called when user makes a "long press" somewhere on the map.
     */
    onLongPress: PropTypes.func,
    /**
     * Callback that is called when user makes a "drag" somewhere on the map
     */
    onPanDrag: PropTypes.func,
    /**
     * Callback that is called when a callout is tapped by the user.
     */
    onCalloutPress: PropTypes.func,
};

class AMapView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            isReady: Platform.OS === 'ios'
        };

        this._onMapLoaded = this._onMapLoaded.bind(this);
        this._onMapPress = this._onMapPress.bind(this);
        this._onChange = this._onChange.bind(this);
        this._onLayout = this._onLayout.bind(this);
    }

    componentWillUpdate(nextProps) {

        const a = this.__lastRegion;
        const b = nextProps.region;

        if (!a || !b) {
            return;
        }

        if (
            a.latitude !== b.latitude ||
            a.longitude !== b.longitude ||
            a.latitudeDelta !== b.latitudeDelta ||
            a.longitudeDelta !== b.longitudeDelta
        ) {
            this.map.setNativeProps({
                region: b
            });
        }
    }

    _onMapLoaded() {
        const {region, initialRegion} = this.props;
        if (region) {
            this.map && this.map.setNativeProps({region})
        } else if (initialRegion) {
            this.map && this.map.setNativeProps({initialRegion})
        }

        this.setState({isReady: true}, () => {
            this.props.onMapLoaded && this.props.onMapLoaded();
        });
    };

    _onMapPress(event) {

    }

    _onChange(event) {
        this.__lastRegion = event.nativeEvent.region;
        if (event.nativeEvent.continuous) {
            this.props.onRegionChange && this.props.onRegionChange(event.nativeEvent.region);
        } else {
            this.props.onRegionChangeComplete && this.props.onRegionChangeComplete(event.nativeEvent.region);
        }
    }

    _onLayout(e) {
        const {layout} = e.nativeEvent;
        if (!layout.width || !layout.height) return;
        if (this.state.isReady && !this.__layoutCalled) {
            const {region, initialRegion} = this.props;
            if (region) {
                this.__layoutCalled = true;
                this.map.setNativeProps({region});
            } else if (initialRegion) {
                this.__layoutCalled = true;
                this.map.setNativeProps({initialRegion})
            }
        }

        this.props.onLayout && this.props.onLayout(e);
    }

    animateToRegion(region, duration) {
        this._runCommand('animateToRegion', [region, duration || 500]);
    }

    animateToCoordinate(latLng, duration) {
        this._runCommand('animateToCoordinate', [latLng, duration || 500]);
    }

    fitToElements(animated) {
        this._runCommand('fitToElements', [animated]);
    }

    fitToSuppliedMarkers(markers, animated) {
        this._runCommand('fitToSuppliedMarkers', [markers, animated]);
    }

    fitToCoordinates(coordinates = [], options) {
        const {
            edgePadding = {top: 0, right: 0, bottom: 0, left: 0},
            animated = true,
        } = options;

        this._runCommand('fitToCoordinates', [coordinates, edgePadding, animated]);
    }

    _uiManagerCommand(name) {
        return NativeModules.UIManager[getAMapName(this.props.provider)].Commands[name]
    }

    _getHandle() {
        return findNodeHandle(this);
    }

    _runCommand(name, args) {
        switch (Platform.OS) {
            case 'android':
                NativeModules.UIManager.dispatchViewManagerCommand(
                    this._getHandle(),
                    this._uiManagerCommand(name),
                    args
                );
                break;
        }
    }

    render() {
        const MapView = getAMapComponent(this.props.provider);

        let props = {
            ...this.props,
            region: null,
            initialRegion: null,
            onLayout: this._onLayout,
            onChange: this._onChange,
            onMapLoaded: this._onMapLoaded,
            onMapPress: this._onMapPress
        };

        return (
            <MapView
                ref={ref => {
                    this.map = ref;
                }}
                {...props}
            />
        );
    }
}

AMapView.propTypes = propTypes;
AMapView.MAP_TYPES = MAP_TYPES;
AMapView.MapMarker = MapMarker;
AMapView.viewConfig = viewConfig;


const nativeComponent = Component => requireNativeComponent(Component, AMapView, {
    nativeOnly: {
        onChange: true
    }
});

const aMaps = {
    default: nativeComponent('AMapView'),
    texture: nativeComponent('ATextureMapView')
};

const getAMapName = provider => {
    if (provider && provider === 'default') {
        return 'AMapView'
    } else {
        return 'ATextureMapView'
    }
};

const getAMapComponent = provider => aMaps[provider || 'default'];

module.exports = AMapView;