import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {
    StyleSheet,
    View,
    ViewPropTypes,
    requireNativeComponent
} from 'react-native';

import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
const viewPropTyeps = ViewPropTypes || View.propTypes;

const propTypes = {
    ...viewPropTyeps,
    identifier: PropTypes.string,
    reuseIdentifier: PropTypes.string,
    title: PropTypes.string,
    description: PropTypes.string,
    image: PropTypes.any,
    opacity: PropTypes.number,
    coordinate: PropTypes.shape({
        latitude: PropTypes.number.isRequired,
        longitude: PropTypes.number.isRequired,
    }).isRequired,
};

class MapMarker extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        let image;
        if (this.props.image) {
            image = resolveAssetSource(this.props.image) || {};
            image = image.uri || this.props.image;
        }

        return (
            <AMapMarker
                ref={ref => {
                    this.marker = ref;
                }}
                {...this.props}
                image={image}
                style={[styles.marker, this.props.style]}
            />
        );
    }
}

MapMarker.propTypes = propTypes;

const styles = StyleSheet.create({
    marker: {
        position: 'absolute',
        backgroundColor: 'transparent',
    },
});

const AMapMarker = requireNativeComponent(`AMapMarker`, MapMarker, {
    nativeOnly: {onChange: true}
});

module.exports = MapMarker;