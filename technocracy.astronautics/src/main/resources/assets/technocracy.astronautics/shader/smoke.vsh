#version 140

in vec2 position;
//in vec3 pos;
//in vec2 rot_scale;
in vec4 maxtime_currenttime_rendertime_rotation;
in float scale;
in vec3 pos;
//in mat4 modelViewMatrix;

out vec2 textureCoords;
out vec4 mt_ct_rt_rot;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

//uniform vec3 pos;
//uniform vec2 rot_scale;

void translate(vec3 vec, inout mat4 src) {
    src[3] += src * vec4(vec, 0f);
    //src[3] += src[0] * vec.x + src[1] * vec.y + src[2] * vec.z;
    /*src[3][0] += src[0][0] * vec.x + src[1][0] * vec.y + src[2][0] * vec.z;
    src[3][1] += src[0][1] * vec.x + src[1][1] * vec.y + src[2][1] * vec.z;
    src[3][2] += src[0][2] * vec.x + src[1][2] * vec.y + src[2][2] * vec.z;
    src[3][3] += src[0][3] * vec.x + src[1][3] * vec.y + src[2][3] * vec.z;*/
}

void scaleMatrix(vec4 vec, inout mat4 src) {
    //src *= mat4(vec, vec, vec, vec4(1f));
    src[0] *= vec;
    src[1] *= vec;
    src[2] *= vec;
}

void rotate(float angle, inout mat4 src) {
    float c = cos(angle);
    float s = sin(angle);

    vec4 first = src[0] * c + src[1] * s;
    vec4 second = src[0] * -s + src[1] * c;

    src[0] = first;
    src[1] = second;


    /*    vec3 axis = vec3(0f, 0f, 1f);
    float c = cos(angle);
    float s = sin(angle);
    float oneminusc = 1.0f - c;
    float xy = axis.x*axis.y;
    float yz = axis.y*axis.z;
    float xz = axis.x*axis.z;
    float xs = axis.x*s;
    float ys = axis.y*s;
    float zs = axis.z*s;

    float f00 = axis.x*axis.x*oneminusc+c;
    float f01 = xy*oneminusc+zs;
    float f02 = xz*oneminusc-ys;
    // n[3] not used
    float f10 = xy*oneminusc-zs;
    float f11 = axis.y*axis.y*oneminusc+c;
    float f12 = yz*oneminusc+xs;
    // n[7] not used
    float f20 = xz*oneminusc+ys;
    float f21 = yz*oneminusc-xs;
    float f22 = axis.z*axis.z*oneminusc+c;

    vec4 first = src[0] * f00 + src[1] * f01 + src[2] * f02;
    vec4 second = src[0] * f10 + src[1] * f11 + src[2] * f12;
    vec4 thrid = src[0] * f20 + src[1] * f21 + src[2] * f22;

    src[0] = first;
    src[1] = second;
    src[2] = thrid;*/
}

mat4 calculateMat() {
    mat4 modelMatrix = mat4(1f);
    translate(pos, modelMatrix);

    mat4 tp = transpose(modelViewMatrix);
    modelMatrix[0].xyz = tp[0].xyz;
    modelMatrix[1].xyz = tp[1].xyz;
    modelMatrix[2].xyz = tp[2].xyz;

    rotate(maxtime_currenttime_rendertime_rotation.w, modelMatrix);

    scaleMatrix(vec4(scale), modelMatrix);

    return modelViewMatrix * modelMatrix;
}

void main(void){
    mt_ct_rt_rot = maxtime_currenttime_rendertime_rotation;
    textureCoords = position + vec2(0.5, 0.5);
    textureCoords.y = 1.0 - textureCoords.y;

    gl_Position = projectionMatrix * calculateMat() * vec4(position, 0.0, 1.0);
}