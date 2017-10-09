import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {
    StyleSheet,
    View,
    ViewPropTypes,
    requireNativeComponent
} from 'react-native';

const viewPropTyeps = ViewPropTypes || View.propTypes;

const propTypes = {
    ...View.propTypes,
    onMapPress: PropTypes.func,
    onChange: PropTypes.func,
    onSelect: PropTypes.func
};

class TestView extends Component {
    constructor(props) {
        super(props);
        this._onMapPress = this._onMapPress.bind(this);
        this._onChange = this._onChange.bind(this);
        this._onSelect = this._onSelect.bind(this);
    }

    _onMapPress(evet) {
        alert("onMapPress");
    }

    _onChange(event) {
        alert("_onChange");
        if (this.props.onChange) {
            this.props.onChange(event);
        }
    }

    _onSelect(event) {
        alert("onSelect")
    }

    render() {
        return (
            <RCTMyCustomView
                ref={ref => {
                    this.marker = ref;
                }}
                {...this.props}
                onMapPress={this._onMapPress}
                onChange={this._onChange}
                onSelect={this._onChange}
            />
        );
    }
}

TestView.propTypes = propTypes;

const RCTMyCustomView = requireNativeComponent(`RCTMyCustomView`, TestView, {
    nativeOnly: {onChange: true}
});

module.exports = RCTMyCustomView;