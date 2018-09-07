const path = require('path'),
    MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = {
    entry: './src/main/resources/web/include/index.js',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'build/resources/main/web/include')
    },
    mode: 'production',
    plugins: [
        new MiniCssExtractPlugin({filename: '[name].css'})
    ],
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: [/node_modules/],
                use: [{
                    loader: 'babel-loader',
                    options: {presets: ['es2015', 'stage-0']}
                }],
            },
            {
                test: /\.less$/,
                use: [MiniCssExtractPlugin.loader, 'css-loader', 'less-loader']
            }
        ],
    }
};