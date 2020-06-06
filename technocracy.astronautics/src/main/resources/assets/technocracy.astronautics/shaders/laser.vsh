#version 120

varying vec2 FragCoord;
uniform vec3 center;
uniform float time;
uniform float scale;
uniform float spread;
uniform float rotateScale;

mat4 rotationX(in float angle) {
    return mat4(1.0, 0, 0, 0,
    0, cos(angle), -sin(angle), 0,
    0, sin(angle), cos(angle), 0,
    0, 0, 0, 1);
}

mat4 rotationY(in float angle) {
    return mat4(cos(angle), 0, sin(angle), 0,
    0, 1.0, 0, 0,
    -sin(angle), 0, cos(angle), 0,
    0, 0, 0, 1);
}

mat4 rotationZ(in float angle) {
    return mat4(cos(angle), -sin(angle), 0, 0,
    sin(angle), cos(angle), 0, 0,
    0, 0, 1, 0,
    0, 0, 0, 1);
}

void main() {
    FragCoord = gl_MultiTexCoord0.st;
    gl_FrontColor = gl_Color;

    vec4 vertex = vec4(gl_Vertex.x - center.x, gl_Vertex.y, gl_Vertex.z - center.z, 1.0);

    if (rotateScale != 0) {
        vertex = vertex * (rotationY(((vertex.y + center.y) * rotateScale + time) / 180.0 * 3.14));
        vertex = vec4(vertex.x + center.x, vertex.y, vertex.z + center.z, 1.0);
    }

    vec3 pos = vertex.xyz;

    vec3 normal = vec3(0.0);
    if (scale != 0) {
        normal = normalize(vec3(center.x - pos.x, 0.0, center.z - pos.z)) * scale;
        normal = normal * sin((pos.y + center.y + time) / spread);
    }

    gl_Position = gl_ModelViewProjectionMatrix * vec4(pos + normal, 1.0);
}