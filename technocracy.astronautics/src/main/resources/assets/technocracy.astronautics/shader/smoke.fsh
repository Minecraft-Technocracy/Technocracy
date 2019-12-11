#version 140

in vec2 textureCoords;

uniform sampler2D smoke;
uniform sampler2D noise;
uniform sampler2D lighting;
/*uniform int maxAge; z
uniform float renderTime; x
uniform float screenTime;* y*/
uniform vec3 values;

void main(void){
    float renderTime = values.x;
    float screenTime = values.y;
    vec4 texel0 = texture(smoke, textureCoords);

    vec4 texel1 = texture(noise, textureCoords + renderTime);
    vec4 texel2 = texture(noise, textureCoords * 0.5 - renderTime);
    vec4 texel3 = texture(noise, textureCoords * 2 + renderTime);

    gl_FragColor = texture(lighting, textureCoords);
    gl_FragColor.a = texel0.a * texel1.a * 2.0 * texel2.a * 2.0 * texel3.a * 2.0;

    //gl_FragColor.a *= 1 - (sin(screenTime / 8f) * 0.4f);
    gl_FragColor.a *= 0.2f;

    if (screenTime <= 35) { //3 * 20
        if (screenTime <= 15) {
            //float colorScale = 1f - ((screenTime/ 15f));
            gl_FragColor.rgb = mix(gl_FragColor.rgb, vec3(1f), vec3(1f));
        } else {
            float colorScale = 1f - ((screenTime - 15f) / 20f);//3 * 20
            //vec3 orange = vec3(0.986f, 0.445f, 0.233f);
            vec3 orange = vec3(250 / 255f, 167 / 255f, 87 / 255f);
            vec3 white = vec3(1f, 1f, 170 / 255f);
            //vec3 red = vec3(0.808f, 0.125f, 0.161f);
            //vec3 mixed = mix(red, orange, vec3(sin(((colorScale) * 3.14159f))));
            vec3 mixed = mix(orange, white, vec3(sin(((colorScale) * 3.14159f))));
            float caped = max(colorScale - 0.2f, 0f);
            gl_FragColor.rgb = mix(gl_FragColor.rgb, mixed, vec3(caped));
        }
    }

    float timeLeft = values.z - screenTime;

    if (timeLeft < 100) {
        gl_FragColor.a *= timeLeft / 100f;
    }
}

/*void main(void){
    vec4 texel0 = texture(smoke, textureCoords);

    float renderTime = values.y;
    float screenTime = values.z;

    vec4 texel1 = texture(noise, textureCoords + renderTime);
    vec4 texel2 = texture(noise, 0.5 * textureCoords - renderTime);
    vec4 texel3 = texture(noise, 2 * textureCoords + renderTime);


    gl_FragColor = texture(lighting, textureCoords);
    gl_FragColor.a = texel0.a * texel1.a * 2.0 * texel2.a * 2.0 * texel3.a * 2.0;


    //gl_FragColor.a *= 1 - (sin(screenTime / 8f) * 0.4f);


    if (screenTime <= 35) { //3 * 20
        //float gamma = 2.2f;
        if (screenTime <= 15) {
            //gamma = 4f;
            //float colorScale = 1f - ((screenTime/ 15f));
            gl_FragColor.rgb = mix(gl_FragColor.rgb, vec3(1f), vec3(1f));
        } else {
            float colorScale = 1f - ((screenTime - 15f) / 20f);//3 * 20
            //vec3 orange = vec3(0.986f, 0.445f, 0.233f);
            vec3 orange = vec3(250 / 255f, 167 / 255f, 87 / 255f);
            vec3 white = vec3(1f, 1f, 170 / 255f);
            //vec3 red = vec3(0.808f, 0.125f, 0.161f);
            //vec3 mixed = mix(red, orange, vec3(sin(((colorScale) * 3.14159f))));
            vec3 mixed = mix(orange, white, sin(((colorScale) * 3.14159f)));
            float caped = max(colorScale - 0.2f, 0f);
            gl_FragColor.rgb = mix(gl_FragColor.rgb, mixed, vec3(caped));
        }

        //gl_FragColor.rgb = pow(gl_FragColor.rgb, vec3(1.0/gamma));
    }

    //gl_FragColor.a = texel0.a * texel1.a * texel2.a * texel3.a * 8.0;
    //gl_FragColor.a *= 0.2f;

    //float timeLeft = maxAge - screenTime;
    float timeLeft = values.x - screenTime;


    if (timeLeft < 100) {
        gl_FragColor.a *= timeLeft / 100f;
    }
}*/