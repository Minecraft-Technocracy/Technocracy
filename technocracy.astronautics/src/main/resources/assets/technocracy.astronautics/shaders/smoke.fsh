#version 150

in vec2 FragCoord;
in vec4 mt_ct_rt_rot;
out vec4 FragColor;

uniform sampler2D smoke;
uniform sampler2D noise;
uniform sampler2D lighting;


vec2 rotateUV(vec2 uv, float rotation) {
    float mid = 0.5;
    float sin = sin(rotation);
    float cos = cos(rotation);
    return vec2(
    cos * (uv.x - mid) + sin * (uv.y - mid) + mid,
    cos * (uv.y - mid) - sin * (uv.x - mid) + mid
    );
}

void main(void){
    float maxtime = mt_ct_rt_rot.x;
    float screenTime = mt_ct_rt_rot.y;
    float renderTime = mt_ct_rt_rot.z;
    float rotation = mt_ct_rt_rot.w;

    vec4 texel0 = texture(smoke, FragCoord);

    vec4 texel1 = texture(noise, FragCoord + renderTime);
    vec4 texel2 = texture(noise, FragCoord * 0.5 - renderTime);
    vec4 texel3 = texture(noise, FragCoord * 2 + renderTime);

    FragColor = texture(lighting, rotateUV(FragCoord, rotation));

    FragColor.a = texel0.a * texel1.a * 2.0 * texel2.a * 2.0 * texel3.a * 2.0;

    //gl_FragColor.a *= 1 - (sin(screenTime / 8f) * 0.4f);
    FragColor.a *= 0.2;

    if (screenTime <= 35) { //3 * 20
        if (screenTime <= 15) {
            //float colorScale = 1f - ((screenTime/ 15f));
            FragColor.rgb = mix(FragColor.rgb, vec3(1.0), vec3(1.0));
        } else {
            float colorScale = 1. - ((screenTime - 15.) / 20.);//3 * 20
            //vec3 orange = vec3(0.986f, 0.445f, 0.233f);
            vec3 orange = vec3(250 / 255., 167 / 255., 87 / 255.);
            vec3 white = vec3(1., 1., 170 / 255.);
            //vec3 red = vec3(0.808f, 0.125f, 0.161f);
            //vec3 mixed = mix(red, orange, vec3(sin(((colorScale) * 3.14159f))));
            vec3 mixed = mix(orange, white, vec3(sin(((colorScale) * 3.14159))));
            float caped = max(colorScale - 0.2, 0.0);
            FragColor.rgb = mix(FragColor.rgb, mixed, vec3(caped));
        }
    }

    float timeLeft = maxtime - screenTime;

    if (timeLeft < 100) {
        FragColor.a *= timeLeft / 100.;
    }

}