
set(OPENCC_SRC_DIR "${PROJECT_SOURCE_DIR}/librime/deps/opencc/src")

set(OPENCC_BINARY_DIR "${PROJECT_BINARY_DIR}/librime/deps/opencc")

set(OPENCC_INCLUDE_DIR "${OPENCC_BINARY_DIR}/include")


set(
        LIBOPENCC_HEADERS
        ${OPENCC_SRC_DIR}/Common.hpp
        ${OPENCC_SRC_DIR}/Config.hpp
        ${OPENCC_SRC_DIR}/Conversion.hpp
        ${OPENCC_SRC_DIR}/ConversionChain.hpp
        ${OPENCC_SRC_DIR}/Converter.hpp
        ${OPENCC_SRC_DIR}/Dict.hpp
        ${OPENCC_SRC_DIR}/DictConverter.hpp
        ${OPENCC_SRC_DIR}/DictEntry.hpp
        ${OPENCC_SRC_DIR}/DictGroup.hpp
        ${OPENCC_SRC_DIR}/Exception.hpp
        ${OPENCC_SRC_DIR}/Export.hpp
        ${OPENCC_SRC_DIR}/Lexicon.hpp
        ${OPENCC_SRC_DIR}/MarisaDict.hpp
        ${OPENCC_SRC_DIR}/MaxMatchSegmentation.hpp
        ${OPENCC_SRC_DIR}/Optional.hpp
        ${OPENCC_SRC_DIR}/PhraseExtract.hpp
        ${OPENCC_SRC_DIR}/Segmentation.hpp
        ${OPENCC_SRC_DIR}/Segments.hpp
        ${OPENCC_SRC_DIR}/SerializableDict.hpp
        ${OPENCC_SRC_DIR}/SerializedValues.hpp
        ${OPENCC_SRC_DIR}/SimpleConverter.hpp
        ${OPENCC_SRC_DIR}/TextDict.hpp
        ${OPENCC_SRC_DIR}/UTF8StringSlice.hpp
        ${OPENCC_SRC_DIR}/UTF8Util.hpp
        ${OPENCC_SRC_DIR}/ResourceProvider.hpp
        ${OPENCC_SRC_DIR}/ConversionInspection.hpp
        ${OPENCC_SRC_DIR}/opencc.h
)

install(
        FILES ${LIBOPENCC_HEADERS}
        DESTINATION ${OPENCC_INCLUDE_DIR}/opencc
)
#file(COPY ${LIBOPENCC_HEADERS}
#        DESTINATION ${OPENCC_INCLUDE_DIR}/opencc)

add_custom_target(copy_opencc_headers ALL
        COMMAND ${CMAKE_COMMAND} -E make_directory ${OPENCC_INCLUDE_DIR}/opencc
        COMMAND ${CMAKE_COMMAND} -E copy_if_different
        ${LIBOPENCC_HEADERS}
        ${OPENCC_INCLUDE_DIR}/opencc
        COMMENT "Copying opencc headers"
)
add_dependencies(libopencc copy_opencc_headers)


#message(FATAL_ERROR "Path ${OPENCC_INCLUDE_DIR}")
