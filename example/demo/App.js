import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    View,
    TouchableOpacity,
    Switch,
    ScrollView
} from 'react-native';
import DisplayLatLng from './DisplayLatLng';
import DefaultMarkers from './DefaultMarkers';

class App extends Component {
    constructor(props) {
        super(props);

        this.state = {
            Component: null,
            useTexture: false
        }
    }

    renderExample([Component, title]) {
        return (
            <TouchableOpacity
                key={title}
                style={styles.button}
                onPress={() => this.setState({Component})}
            >
                <Text>{title}</Text>
            </TouchableOpacity>
        )
    }

    renderBackButton() {
        return (
            <TouchableOpacity
                style={styles.back}
                onPress={() => this.setState({Component: null})}
            >
                <Text style={{fontWeight: 'bold', fontSize: 30}}>&larr;</Text>
            </TouchableOpacity>
        );
    }

    renderGoogleSwtich() {
        return (
            <View>
                <Text>Use TextureMap</Text>
                <Switch
                    onValueChange={(value) => this.setState({useTexture: value})}
                    style={{marginBottom: 10}}
                    value={this.state.useTexture}/>
            </View>
        );
    }

    renderExamples(examples) {
        const {
            Component,
            useTexture
        } = this.state;

        return (
            <View style={styles.container}>
                {Component && <Component provider={useTexture ? 'texture' : null}/>}
                {Component && this.renderBackButton()}
                {!Component &&
                <ScrollView
                    style={StyleSheet.absoluteFill}
                    contentContainerStyle={styles.scrollview}
                    showsVerticalScrollIndicator={false}
                >
                    {this.renderGoogleSwtich()}
                    {examples.map(example => this.renderExample(example))}
                </ScrollView>
                }
            </View>
        );
    }

    render() {
        return this.renderExamples([
            [DisplayLatLng, 'Tracking Position'],
            [DefaultMarkers, 'Default Markers'],
        ])
    }
}

const styles = StyleSheet.create({
    container: {
        ...StyleSheet.absoluteFillObject,
        justifyContent: 'flex-end',
        alignItems: 'center',
    },
    scrollview: {
        alignItems: 'center',
        paddingVertical: 40,
    },
    button: {
        flex: 1,
        marginTop: 10,
        backgroundColor: 'rgba(220,220,220,0.7)',
        paddingHorizontal: 18,
        paddingVertical: 12,
        borderRadius: 20,
    },
    back: {
        position: 'absolute',
        top: 20,
        left: 12,
        backgroundColor: 'rgba(255,255,255,0.4)',
        padding: 12,
        borderRadius: 20,
        width: 80,
        alignItems: 'center',
        justifyContent: 'center',
    },
});

module.exports = App;
