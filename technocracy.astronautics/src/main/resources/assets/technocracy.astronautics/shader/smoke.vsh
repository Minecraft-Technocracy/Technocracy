#version 140

in vec2 position;

out vec2 textureCoords;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

uniform vec3 pos;
uniform vec2 rot_scale;

void translate(vec3 vec, inout mat4 src) {
    src[3][0] += src[0][0] * vec.x + src[1][0] * vec.y + src[2][0] * vec.z;
    src[3][1] += src[0][1] * vec.x + src[1][1] * vec.y + src[2][1] * vec.z;
    src[3][2] += src[0][2] * vec.x + src[1][2] * vec.y + src[2][2] * vec.z;
    src[3][3] += src[0][3] * vec.x + src[1][3] * vec.y + src[2][3] * vec.z;
}

void scale(vec4 vec, inout mat4 src) {
    src[0] *= vec;
    src[1] *= vec;
    src[2] *= vec;
}

void rotate(float angle, inout mat4 src) {
    vec3 axis = vec3(0f, 0f, 1f);
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
    src[2] = thrid;

    /*float t00 = src[0][0] * f00 + src[1][0] * f01 + src[2][0] * f02;
    float t01 = src[0][1] * f00 + src[1][1] * f01 + src[2][1] * f02;
    float t02 = src[0][2] * f00 + src[1][2] * f01 + src[2][2] * f02;
    float t03 = src[0][3] * f00 + src[1][3] * f01 + src[2][3] * f02;*/
    /*float t10 = src[0][0] * f10 + src[1][0] * f11 + src[2][0] * f12;
    float t11 = src[0][1] * f10 + src[1][1] * f11 + src[2][1] * f12;
    float t12 = src[0][2] * f10 + src[1][2] * f11 + src[2][2] * f12;
    float t13 = src[0][3] * f10 + src[1][3] * f11 + src[2][3] * f12;*/
    /*src[2][0] = src[0][0] * f20 + src[1][0] * f21 + src[2][0] * f22;
    src[2][1] = src[0][1] * f20 + src[1][1] * f21 + src[2][1] * f22;
    src[2][2] = src[0][2] * f20 + src[1][2] * f21 + src[2][2] * f22;
    src[2][3] = src[0][3] * f20 + src[1][3] * f21 + src[2][3] * f22;
    src[0][0] = t00;
    src[0][1] = t01;
    src[0][2] = t02;
    src[0][3] = t03;
    src[1][0] = t10;
    src[1][1] = t11;
    src[1][2] = t12;
    src[1][3] = t13;*/
}

mat4 calculateMat() {
    mat4 modelMatrix = mat4(1f);
    translate(pos, modelMatrix);

    modelMatrix[0][0] = modelViewMatrix[0][0];
    modelMatrix[0][1] = modelViewMatrix[1][0];
    modelMatrix[0][2] = modelViewMatrix[2][0];
    modelMatrix[1][0] = modelViewMatrix[0][1];
    modelMatrix[1][1] = modelViewMatrix[1][1];
    modelMatrix[1][2] = modelViewMatrix[2][1];
    modelMatrix[2][0] = modelViewMatrix[0][2];
    modelMatrix[2][1] = modelViewMatrix[1][2];
    modelMatrix[2][2] = modelViewMatrix[2][2];

    rotate(rot_scale.x, modelMatrix);
    scale(vec4(rot_scale.y),modelMatrix);

    return modelViewMatrix * modelMatrix;
}

void main(void){

    textureCoords = position + vec2(0.5, 0.5);
    textureCoords.y = 1.0 - textureCoords.y;

    gl_Position = projectionMatrix * calculateMat() * vec4(position, 0.0, 1.0);

}